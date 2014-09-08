package bike.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.LinkedHashMap;

public class GameClient implements Runnable {
	private InetAddress address;
	private int port;
	private Socket socket;
	
	public GameClient(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			this.socket = new Socket(this.address, this.port);
			while (true) {
				Thread.sleep(300000);
			}
		} catch (IOException|InterruptedException e) {
			System.out.println("CLIENT ERROR");
		} finally {
			try {
				if (this.socket != null) {
					this.socket.close();
				}
			} catch (IOException e) {
				System.out.println("CLIENT: FATAL ERROR");
			}
		}
		
		System.out.println("CLIENT DONE");
	}
	
	public String getData() {
		if (this.socket == null) {
			return "";
		}
		
		StringBuilder data = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				data.append(line);
			}
		} catch (IOException e) {
			data = new StringBuilder();
		}
		
		return data.toString();
	}
	
	public void sendData(String data) {
		if (this.socket == null) {
			return;
		}
		
		try (PrintStream ps = new PrintStream(this.socket.getOutputStream(), true, "UTF-8")) {
			ps.print(data);
		} catch (IOException e) {}
	}
	
	// Assuming colon separated key-value pairs
	public Map<String,String> getMappedData() {
		Map<String,String> results = new LinkedHashMap<String,String>();
		String data = this.getData();
		
		for (String line : data.split("\\n")) {
			int delimiterIndex = data.indexOf(':');
			results.put(line.substring(0, delimiterIndex), line.substring(delimiterIndex+1));
		}
		
		return results;
	}
	
	public void sendMappedData(Map<String,String> data) {
		StringBuilder content = new StringBuilder();
		
		for (Map.Entry<String,String> entry : data.entrySet()) {
			content.append(entry.getKey() + ":" + entry.getValue() + "\n");
		}
		
		this.sendData(content.toString());
	}
}
