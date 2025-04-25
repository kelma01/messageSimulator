import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPSender extends MainUI implements Runnable {
    private final int targetPort;
    private final String targetIp;
    private final byte[] message;
    private int msgNumber;

    UDPSender(String targetIp, int targetPort, byte[] message, int msgNumber) {
        this.targetPort = targetPort;
        this.targetIp = targetIp;
        this.message = message;
        this.msgNumber = 0;
    }

    @Override
    public void run(){
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress targetAddress = InetAddress.getByName(targetIp);
            DatagramPacket sendPacket = new DatagramPacket(this.message, this.message.length, targetAddress, targetPort);
            clientSocket.send(sendPacket);

            StringBuilder s = new StringBuilder();
            for(int i=0; i<sendPacket.getLength(); i++){
                String tmp = String.format("%02X", message[i]) + " ";
                s.append(tmp);
            }
            log(msgNumber + " - Sent Message: " + s, "SEND");
        }
        catch (Exception e) {
            log(e.toString(), "ERROR");
        }
    }
}