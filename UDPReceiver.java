import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class UDPReceiver extends MainUI implements Runnable {
    private final int port;
    private final int targetPort;
    private int msgNumber = 0;
    private boolean flag = false;
    private DatagramSocket socket;

    public UDPReceiver(int port, int targetPort) {
        this.port = port;
        this.targetPort = targetPort;
    }

    @Override
    public void run(){
        try{
            socket = new DatagramSocket(port);
            
            byte[] buffer = new byte[1024];

            while (flag) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                StringBuilder s = new StringBuilder();
                for(byte b : buffer){
                    s.append(String.format("%02X", b));
                    s.append(" ");
                }
                log(msgNumber + " - Received Message: " + s, "RECEIVE");

                new Thread(new UDPSender("127.0.0.1", targetPort, buffer, msgNumber++)).start();
            }
        } 
        catch (Exception e) {
            log(e.toString(), "ERROR");
        }
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public DatagramSocket getSocket() {
        return this.socket;
    }
}
