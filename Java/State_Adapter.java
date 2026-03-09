package Java;

public class State_Adapter {
    interface MediaPlayer {
        void setState(State state);

        void play();
    }

    interface State {
        void pressPlay(MediaPlayer mp);
    }

    static class StoppedState implements State {
        public StoppedState() {
            System.out.println("state - stopped ");
        }

        @Override
        public void pressPlay(MediaPlayer mp) {
            mp.setState(new PlayingState());
        }
    }

    static class PlayingState implements State {
        public PlayingState() {
            System.out.println("state - playing ");
        }

        @Override
        public void pressPlay(MediaPlayer mp) {
            mp.setState(new StoppedState());
        }
    }

    static class Mp4Player {

        void play() {
            System.out.println("playing mp4");
        }
    }

    static class Mp4Adapter implements MediaPlayer {
        Mp4Player mp = new Mp4Player();
        State state = new StoppedState();

        @Override
        public void play() {
            mp.play();
            state.pressPlay(this);
        }

        @Override
        public void setState(State state) {
            this.state = state;
        }
    }

    public static void main(String[] a) {
        MediaPlayer mp = new Mp4Adapter();
        mp.play();
    }
}
