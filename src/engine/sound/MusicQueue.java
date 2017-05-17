package engine.sound;

public class MusicQueue {
	
	private MusicNode queue = null;
	
	public void addMusic(Music music) {
		if (queue == null) {
			MusicNode node = new MusicNode();
			node.music = music;
			node.next = null;
			queue = node;
		} else {
			while (queue.next != null) {
				queue = queue.next;
			}
			MusicNode node = new MusicNode();
			node.music = music;
			node.next = null;
			queue.next = node;
		}
	}
	
	public void addMusic(int index, Music music) {
		if (queue == null) {
			MusicNode node = new MusicNode();
			node.music = music;
			node.next = null;
			queue = node;
		} else {
			int index0 = 0;
			while (queue.next != null && index0 < index) {
				queue = queue.next;
				index0++;
			}
			if (queue.next != null) {
				MusicNode node = new MusicNode();
				node.music = music;
				node.next = queue.next;
				queue.next = node;
			} else {
				MusicNode node = new MusicNode();
				node.music = music;
				node.next = null;
				queue.next = node;
			}
		}
	}
	
	public void play() {
		if (queue != null) {
			queue.music.play();
		}
	}
	
	public void update() {
		if (queue != null) {
			if (!queue.music.update()) {
				queue.music.stop();
				queue = queue.next;
			}
		}
	}
	
	public void stop() {
		if (queue != null) {
			queue.music.stop();
		}
	}
	
	private class MusicNode {
		
		private Music music;
		
		private MusicNode next;
		
	}

}
