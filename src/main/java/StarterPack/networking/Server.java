package StarterPack.networking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
  private final int portNumber;
  private final List<Socket> clientSockets;
  private ServerSocket serverSocket;
  private boolean open;
  private List<BufferedReader> inList;
  private List<PrintWriter> outList;

  private BufferedReader in0;
  private PrintWriter out0;

  private int clientCount;


  public Server(int portNumber, int clientCount) {
    this.portNumber = portNumber;
    this.open = false;
    this.clientSockets = Arrays.asList(new Socket[clientCount]);
    this.clientCount = clientCount;
  }

  /** Starts a server at the port number passed into the constructor. */
  public void open() {
    try {
      this.serverSocket = new ServerSocket(portNumber);
      for (int i = 0; i < clientCount; i++) {
          clientSockets.set(i, serverSocket.accept());
      }
      this.open = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    inList = new ArrayList<>();
    outList = new ArrayList<>();
//    try {
//      in0 = new BufferedReader(new InputStreamReader(clientSockets.get(0).getInputStream()));
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//    try {
//         out0 = new PrintWriter(clientSockets.get(0).getOutputStream(), true);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }

    for (int i = 0; i < clientCount; i++) {
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSockets.get(i).getInputStream()));
        inList.add(in);
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        PrintWriter out = new PrintWriter(clientSockets.get(i).getOutputStream(), true);
        outList.add(out);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

  /**
   * Reads a single line from a server at the port number passed into the constructor.
   *
   * @return Line read from server.
   */
  public List<String> readAll() {
    try {
      List<String> reads = new ArrayList<>();
      for (int i = 0; i < clientCount; i++) {
        BufferedReader in = inList.get(i);
        String line;
        if ((line = in.readLine()) != null) {
          reads.add(line);
        }
      }
      return reads;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

//  public String read() {
//      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return "error read";
//  }

  /**
   * Writes an object as JSON into the server's output stream.
   *
   * @param obj Object to be written.
   */
  public void writeAll(Object obj) {
    try {
      writeAll(new ObjectMapper().writeValueAsString(obj));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes a string into the server's output stream.
   *
   * @param string String to be written.
   */
  public void writeAll(String string) {
    for (int i = 0; i < clientCount; i++) {
      PrintWriter out = outList.get(i);
      out.println(string);
    }

  }

  /**
   * Writes a string into the server's output stream.
   *
   * @param string String to be written.
   */
  public void write(String string, int i) {
    PrintWriter out = outList.get(i);
    out.println(string);
  }

  public void write0(String string) {
    outList.get(0).println(string);
  }

  public String read0() {
    try {
      return inList.get(0).readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  public boolean isOpen() {
    return open;
  }

  public void close() {
    try {
      serverSocket.close();
      this.open = false;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
