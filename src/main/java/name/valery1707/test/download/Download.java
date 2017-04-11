package name.valery1707.test.download;

import org.apache.commons.io.output.DeferredFileOutputStream;

class Download {
	private final Source source;
	private final DeferredFileOutputStream stream;
	private final long time;

	Download(Source source, DeferredFileOutputStream stream, long time) {
		this.source = source;
		this.stream = stream;
		this.time = time;
	}

	public Source getSource() {
		return source;
	}

	public DeferredFileOutputStream getStream() {
		return stream;
	}

	public long getTime() {
		return time;
	}
}
