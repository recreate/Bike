package bike.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GameServer implements Runnable {
	private InetAddress address;
	private int port;
	private int maxConnections;
	private ServerSocket serverSocket;
	protected List<Socket> connections;
	
	public GameServer(InetAddress address, int port, int maxConnections) {
		this.address = address;
		this.port = port;
		this.maxConnections = maxConnections;
		this.connections = new ArrayList<Socket>();
	}
	
	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(this.port);
			
			for (int i = 0; i < this.maxConnections; i++) {
				Socket clientSocket = serverSocket.accept();
				this.connections.add(clientSocket);
			}
			
			while (true) {
				Thread.sleep(300000);
			}
		} catch(IOException|InterruptedException e) {
			System.out.println("SERVER ERROR");
		} finally {
			try {
				if (this.serverSocket != null)
					this.serverSocket.close();
				
				for (Socket s : this.connections) {
					if (s != null)
						s.close();
				}
			} catch (IOException e) {
				System.out.println("SERVER: FATAL ERROR");
			}
		}
		
		System.out.println("SERVER DONE");
	}
	
	public int getNumConcurrentConnections() {
		return this.connections.size();
	}
	
	public String getData() {
		String result = "";
		
		for (Socket s : this.connections) {
			StringBuilder data = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));) {
				String line = "";
				while ((line = br.readLine()) != null) {
					data.append(line + "\n");
				}
			} catch (IOException e) {
				data = new StringBuilder();
			}
			
			if (data.length() != 0) {
				result = data.toString();
				break;
			}
		}
		
		return result;
	}
	
	public void broadcastData(String data) {
		for (Socket s : this.connections) {
			try (PrintWriter pw = new PrintWriter(s.getOutputStream(), true);) {
				pw.print(data);
			} catch (IOException e) {}
		}
	}
	
	// Assuming colon separated key-value pairs
	public Map<String,String> getMappedData() {
		Map<String,String> results = new LinkedHashMap<String,String>();
		String data = this.getData();
		
		if (!data.isEmpty()) {
			for (String line : data.split("\\n")) {
				int delimiterIndex = line.indexOf(':');
				results.put(line.substring(0, delimiterIndex), line.substring(delimiterIndex+1));
			}
		}
		
		return results;
	}
	
	public void broadcastMappedData(Map<String,String> data) {
		StringBuilder content = new StringBuilder();
		
		for (Map.Entry<String,String> entry : data.entrySet()) {
			content.append(entry.getKey() + ":" + entry.getValue() + "\n");
		}
		
		this.broadcastData(content.toString());
	}
}
