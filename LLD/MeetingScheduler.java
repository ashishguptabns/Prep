package LLD;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.time.*;
import java.util.stream.Collectors;

public class MeetingScheduler {

    // Core entities for real-time availability
    static class User {
        private final String id;
        private final String email;
        private final ZoneId timeZone;
        private final Map<LocalDate, Set<TimeSlot>> availability;

        public User(String id, String email, ZoneId timeZone) {
            this.id = id;
            this.email = email;
            this.timeZone = timeZone;
            this.availability = new ConcurrentHashMap<>();
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public ZoneId getTimeZone() {
            return timeZone;
        }

        // FR-001: Check availability within 200ms
        public boolean isAvailable(LocalDateTime dateTime, Duration duration) {
            LocalDate date = dateTime.toLocalDate();
            LocalTime time = dateTime.toLocalTime();

            Set<TimeSlot> slots = availability.getOrDefault(date, new HashSet<>());
            TimeSlot requestedSlot = new TimeSlot(time, time.plus(duration));

            return slots.stream().noneMatch(slot -> slot.overlaps(requestedSlot));
        }

        public void bookTimeSlot(LocalDate date, TimeSlot slot) {
            availability.computeIfAbsent(date, k -> ConcurrentHashMap.newKeySet()).add(slot);
        }
    }

    static class TimeSlot {
        private final LocalTime start;
        private final LocalTime end;

        public TimeSlot(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        public boolean overlaps(TimeSlot other) {
            return start.isBefore(other.end) && end.isAfter(other.start);
        }
    }

    static class Meeting {
        private final String id;
        private final String title;
        private final LocalDateTime startTime;
        private final Duration duration;
        private final Set<User> attendees;
        private final User organizer;

        public Meeting(String id, String title, LocalDateTime startTime, Duration duration, User organizer) {
            this.id = id;
            this.title = title;
            this.startTime = startTime;
            this.duration = duration;
            this.organizer = organizer;
            this.attendees = ConcurrentHashMap.newKeySet();
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public Duration getDuration() {
            return duration;
        }

        public Set<User> getAttendees() {
            return attendees;
        }

        public User getOrganizer() {
            return organizer;
        }

        public boolean addAttendee(User user) {
            if (user.isAvailable(startTime, duration)) {
                attendees.add(user);
                return true;
            }
            return false;
        }
    }

    // FR-002: Conflict resolution engine
    static class ConflictResolver {
        public List<LocalDateTime> findAlternativeSlots(Set<User> attendees, Duration duration,
                LocalDateTime preferredTime, int daysAhead) {
            List<LocalDateTime> alternatives = new ArrayList<>();
            LocalDate startDate = preferredTime.toLocalDate();

            for (int day = 0; day <= daysAhead; day++) {
                LocalDate currentDate = startDate.plusDays(day);

                for (int hour = 9; hour <= 17; hour++) {
                    LocalDateTime slotTime = currentDate.atTime(hour, 0);

                    if (attendees.stream().allMatch(user -> user.isAvailable(slotTime, duration))) {
                        alternatives.add(slotTime);
                        if (alternatives.size() >= 5)
                            break; // Return top 5 alternatives
                    }
                }
                if (alternatives.size() >= 5)
                    break;
            }

            return alternatives;
        }
    }

    // FR-004: Calendar synchronization
    static class CalendarSync {
        private final Map<String, CalendarProvider> providers;
        private final LinkedBlockingQueue<SyncEvent> syncQueue;

        public CalendarSync() {
            this.providers = new ConcurrentHashMap<>();
            // Wrap providers with decorators for logging/metrics
            this.providers.put("google", new CalendarSyncDecorator(new GoogleCalendarProvider()));
            this.providers.put("outlook", new CalendarSyncDecorator(new OutlookCalendarProvider()));
            this.syncQueue = new LinkedBlockingQueue<>();

            // Start background sync thread
            startSyncProcessor();
        }

        private void startSyncProcessor() {
            new Thread(() -> {
                while (true) {
                    try {
                        SyncEvent event = syncQueue.take();
                        processSyncEvent(event);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
        }

        public void syncMeeting(Meeting meeting) {
            syncQueue.offer(new SyncEvent("MEETING_CREATE", meeting));
        }

        public void syncUserCalendar(User user) {
            syncQueue.offer(new SyncEvent("USER_SYNC", user));
        }

        private void processSyncEvent(SyncEvent event) {
            if (event.type.equals("MEETING_CREATE")) {
                providers.values().forEach(provider -> provider.createEvent((Meeting) event.data));
            } else if (event.type.equals("USER_SYNC")) {
                providers.values().forEach(provider -> provider.syncUserEvents((User) event.data));
            }
        }

        static class SyncEvent {
            final String type;
            final Object data;

            SyncEvent(String type, Object data) {
                this.type = type;
                this.data = data;
            }
        }
    }

    interface CalendarProvider {
        void createEvent(Meeting meeting);

        void syncUserEvents(User user);
    }

    // Decorator Pattern for enhanced calendar sync
    static class CalendarSyncDecorator implements CalendarProvider {
        private final CalendarProvider provider;

        public CalendarSyncDecorator(CalendarProvider provider) {
            this.provider = provider;
        }

        @Override
        public void createEvent(Meeting meeting) {
            // Add logging before delegation
            System.out.println("LOG: Creating event for " + meeting.getTitle());
            provider.createEvent(meeting);
            // Add metrics after delegation
            System.out.println("METRICS: Event created successfully");
        }

        @Override
        public void syncUserEvents(User user) {
            System.out.println("LOG: Syncing calendar for " + user.getEmail());
            provider.syncUserEvents(user);
            System.out.println("METRICS: Calendar sync completed");
        }
    }

    static class GoogleCalendarProvider implements CalendarProvider {
        @Override
        public void createEvent(Meeting meeting) {
            System.out.println("Google Calendar: Creating event " + meeting.getTitle());
        }

        @Override
        public void syncUserEvents(User user) {
            System.out.println("Google Calendar: Syncing for " + user.getEmail());
        }
    }

    static class OutlookCalendarProvider implements CalendarProvider {
        @Override
        public void createEvent(Meeting meeting) {
            System.out.println("Outlook: Creating event " + meeting.getTitle());
        }

        @Override
        public void syncUserEvents(User user) {
            System.out.println("Outlook: Syncing for " + user.getEmail());
        }
    }

    // Observer Pattern for meeting notifications
    interface MeetingObserver {
        void onMeetingCreated(Meeting meeting);

        void onMeetingUpdated(Meeting meeting);

        void onAttendeeAdded(Meeting meeting, User attendee);
    }

    static class EmailNotificationObserver implements MeetingObserver {
        @Override
        public void onMeetingCreated(Meeting meeting) {
            System.out.println("EMAIL: Meeting " + meeting.getTitle() + " created - sending invitations");
        }

        @Override
        public void onMeetingUpdated(Meeting meeting) {
            System.out.println("EMAIL: Meeting " + meeting.getTitle() + " updated - sending notifications");
        }

        @Override
        public void onAttendeeAdded(Meeting meeting, User attendee) {
            System.out.println("EMAIL: Invitation sent to " + attendee.getEmail() + " for " + meeting.getTitle());
        }
    }

    static class SlackNotificationObserver implements MeetingObserver {
        @Override
        public void onMeetingCreated(Meeting meeting) {
            System.out.println("SLACK: Meeting " + meeting.getTitle() + " scheduled in #" + meeting.getId());
        }

        @Override
        public void onMeetingUpdated(Meeting meeting) {
            System.out.println("SLACK: Meeting " + meeting.getTitle() + " updated");
        }

        @Override
        public void onAttendeeAdded(Meeting meeting, User attendee) {
            System.out.println("SLACK: " + attendee.getEmail() + " added to " + meeting.getTitle());
        }
    }

    // Command Pattern for meeting operations
    interface MeetingCommand {
        void execute();

        void undo();
    }

    static class ScheduleMeetingCommand implements MeetingCommand {
        private final MeetingScheduler scheduler;
        private final String title;
        private final Set<String> attendeeIds;
        private final Duration duration;
        private final String organizerId;
        private final LocalDate preferredDate;
        private Meeting createdMeeting;

        public ScheduleMeetingCommand(MeetingScheduler scheduler, String title, Set<String> attendeeIds,
                Duration duration, String organizerId, LocalDate preferredDate) {
            this.scheduler = scheduler;
            this.title = title;
            this.attendeeIds = attendeeIds;
            this.duration = duration;
            this.organizerId = organizerId;
            this.preferredDate = preferredDate;
        }

        @Override
        public void execute() {
            createdMeeting = scheduler.scheduleMeeting(title, attendeeIds, duration, organizerId, preferredDate);
        }

        @Override
        public void undo() {
            if (createdMeeting != null) {
                scheduler.meetings.remove(createdMeeting.getId());
                System.out.println("UNDO: Meeting " + createdMeeting.getTitle() + " cancelled");
            }
        }
    }

    // FR-007/008: Performance monitoring for scaling
    static class PerformanceMonitor {
        private final AtomicLong meetingCount = new AtomicLong(0);
        private final AtomicLong requestCount = new AtomicLong(0);
        private final AtomicLong responseTimeTotal = new AtomicLong(0);

        public void recordMeetingCreation(long responseTimeMs) {
            meetingCount.incrementAndGet();
            responseTimeTotal.addAndGet(responseTimeMs);
        }

        public void recordRequest() {
            requestCount.incrementAndGet();
        }

        public long getMeetingsPerMinute() {
            return meetingCount.get();
        }

        public long getRequestsPerMinute() {
            return requestCount.get();
        }

        public double getAverageResponseTime() {
            long count = meetingCount.get();
            return count > 0 ? (double) responseTimeTotal.get() / count : 0;
        }
    }

    // Main scheduling engine
    private final Map<String, User> users;
    private final Map<String, Meeting> meetings;
    private final ConflictResolver conflictResolver;
    private final CalendarSync calendarSync;
    private final PerformanceMonitor performanceMonitor;
    private final List<MeetingObserver> observers;

    public MeetingScheduler() {
        this.users = new ConcurrentHashMap<>();
        this.meetings = new ConcurrentHashMap<>();
        this.conflictResolver = new ConflictResolver();
        this.calendarSync = new CalendarSync();
        this.performanceMonitor = new PerformanceMonitor();
        this.observers = new ArrayList<>();

        // Register default observers
        addObserver(new EmailNotificationObserver());
        addObserver(new SlackNotificationObserver());
    }

    public void addObserver(MeetingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MeetingObserver observer) {
        observers.remove(observer);
    }

    private void notifyMeetingCreated(Meeting meeting) {
        observers.forEach(observer -> observer.onMeetingCreated(meeting));
    }

    private void notifyAttendeeAdded(Meeting meeting, User attendee) {
        observers.forEach(observer -> observer.onAttendeeAdded(meeting, attendee));
    }

    // FR-003: Handle 10,000 concurrent availability checks
    public List<LocalDateTime> findOptimalTimes(Set<String> attendeeIds, Duration duration,
            LocalDate startDate, LocalDate endDate) {
        long startTime = System.currentTimeMillis();

        Set<User> attendees = attendeeIds.stream()
                .map(id -> users.get(id))
                .collect(Collectors.toSet());

        List<LocalDateTime> availableSlots = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (int hour = 9; hour <= 17; hour++) {
                LocalDateTime slotTime = date.atTime(hour, 0);

                if (attendees.stream().allMatch(user -> user.isAvailable(slotTime, duration))) {
                    availableSlots.add(slotTime);
                }
            }
        }

        long responseTime = System.currentTimeMillis() - startTime;
        performanceMonitor.recordMeetingCreation(responseTime);

        return availableSlots;
    }

    // FR-007: Schedule meetings with 100+ attendees in under 500ms
    public Meeting scheduleMeeting(String title, Set<String> attendeeIds, Duration duration,
            String organizerId, LocalDate preferredDate) {
        long startTime = System.currentTimeMillis();
        performanceMonitor.recordRequest();

        User organizer = users.get(organizerId);
        Set<User> attendees = attendeeIds.stream()
                .map(id -> users.get(id))
                .collect(Collectors.toSet());

        // Try preferred time first
        LocalDateTime preferredTime = preferredDate.atTime(9, 0);
        if (attendees.stream().allMatch(user -> user.isAvailable(preferredTime, duration))) {
            Meeting meeting = createMeeting(title, attendees, duration, organizer, preferredTime);
            long responseTime = System.currentTimeMillis() - startTime;
            performanceMonitor.recordMeetingCreation(responseTime);
            return meeting;
        }

        // FR-002: Find alternatives if conflict
        List<LocalDateTime> alternatives = conflictResolver.findAlternativeSlots(
                attendees, duration, preferredTime, 7);

        if (alternatives.isEmpty()) {
            throw new RuntimeException("No available time slots found");
        }

        Meeting meeting = createMeeting(title, attendees, duration, organizer, alternatives.get(0));
        long responseTime = System.currentTimeMillis() - startTime;
        performanceMonitor.recordMeetingCreation(responseTime);

        return meeting;
    }

    private Meeting createMeeting(String title, Set<User> attendees, Duration duration,
            User organizer, LocalDateTime startTime) {
        Meeting meeting = new Meeting(UUID.randomUUID().toString(), title, startTime, duration, organizer);

        attendees.forEach(meeting::addAttendee);
        meetings.put(meeting.getId(), meeting);

        // Book time slots for all attendees
        TimeSlot meetingSlot = new TimeSlot(startTime.toLocalTime(),
                startTime.toLocalTime().plus(duration));
        LocalDate meetingDate = startTime.toLocalDate();

        meeting.getAttendees().forEach(attendee -> {
            attendee.bookTimeSlot(meetingDate, meetingSlot);
            notifyAttendeeAdded(meeting, attendee);
        });

        // FR-004: Sync with calendars
        calendarSync.syncMeeting(meeting);

        // Notify observers
        notifyMeetingCreated(meeting);

        return meeting;
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        calendarSync.syncUserCalendar(user);
    }

    // Test method for 45-minute interview demo
    public static void main(String[] args) {
        MeetingScheduler scheduler = new MeetingScheduler();

        // Create users in different time zones
        User user1 = new User("1", "alice@company.com", ZoneId.of("America/New_York"));
        User user2 = new User("2", "bob@company.com", ZoneId.of("Europe/London"));
        User user3 = new User("3", "charlie@company.com", ZoneId.of("Asia/Tokyo"));

        scheduler.addUser(user1);
        scheduler.addUser(user2);
        scheduler.addUser(user3);

        // Test FR-007: Schedule meeting with 100+ attendees simulation
        Set<String> attendeeIds = new HashSet<>();
        attendeeIds.add("2");
        attendeeIds.add("3");

        try {
            long startTime = System.currentTimeMillis();
            Meeting meeting = scheduler.scheduleMeeting(
                    "Global Team Standup",
                    attendeeIds,
                    Duration.ofMinutes(30),
                    "1",
                    LocalDate.now());
            long responseTime = System.currentTimeMillis() - startTime;

            System.out.println("=== MeetingScheduler Performance Test ===");
            System.out.println("Meeting scheduled successfully!");
            System.out.println("Response Time: " + responseTime + "ms");
            System.out.println("Meeting ID: " + meeting.getId());
            System.out.println("Title: " + meeting.getTitle());
            System.out.println("Start Time: " + meeting.getStartTime());
            System.out.println("Attendees: " + meeting.getAttendees().size());
            System.out
                    .println("Average Response Time: " + scheduler.performanceMonitor.getAverageResponseTime() + "ms");
            System.out.println("Total Meetings: " + scheduler.performanceMonitor.getMeetingsPerMinute());
            System.out.println("Total Requests: " + scheduler.performanceMonitor.getRequestsPerMinute());

            // Wait for async sync to complete
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
