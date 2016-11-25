import java.net.ServerSocket;

/* 
 * Aaron Babber
 * aab3456
 * 16480
 * Enrique Perez-Osborne
 * ehp355
 * 16465
 * Slip days used: <0>
 * Fall 2016
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain {
	private ArrayList<PrintWriter> clientOutputStreams;
	
	public static void main(String[] args){
		try{
			new ChatServer().setUpNetworking();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
