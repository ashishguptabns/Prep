package Java;

public class Adapter {
    interface MediaPlayer {
        void play();
    }

    static class Mp4Player {
        void play() {
            System.out.println("playing mp4");
        }
    }

    static class Mp4Adapter implements MediaPlayer {
        Mp4Player mp = new Mp4Player();

        @Override
        public void play() {
            mp.play();
        }
    }

    public static void main(String[] a) {
        MediaPlayer mp = new Mp4Adapter();
        mp.play();
    }
}
