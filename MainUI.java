import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.File;

public class MainUI implements Runnable {
    
    private TextField udpClientPortField;
    private TextField udpServerPortField;
    private TextField udpServerAddressField;
    private static TextArea logArea;
    private static TextArea errorLogArea;
    private static TextArea sendLogArea;
    private static TextArea receiveLogArea;
    private Button startButton;
    private Button stopButton;
    private Button loadButton;
    private Thread udpReceiverThread;
    private UDPReceiver udpReceiver;

    public static void main(String[] args) {
        launch(args);
    }

    @Override 
    public void start(Stage primaryStage) {
        udpClientPortField = new TextField();
        udpServerPortField = new TextField();
        udpServerAddressField = new TextField();
        logArea = new TextArea();
        errorLogArea = new TextArea();
        sendLogArea = new TextArea();
        receiveLogArea = new TextArea();
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        loadButton = new Button("Load");

        logArea.setEditable(false);
        errorLogArea.setEditable(false);
        sendLogArea.setEditable(false);
        receiveLogArea.setEditable(false);

        stopButton.setDisable(true);

        startButton.setPrefSize(150, 60);
        stopButton.setPrefSize(150, 60);
        loadButton.setPrefSize(150, 60);

        VBox inputBox = new VBox(10,
            new Label("UDP Client Port: "), udpClientPortField,
            new Label("UDP Server Port: "), udpServerPortField,
            new Label("UDP Server Address: "), udpServerAddressField
        );

        VBox buttonBox = new VBox(10, 
            startButton,
            stopButton, 
            loadButton
        );
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefWidth(400);
        buttonBox.setSpacing(20);
        
        VBox errorLogAreaBox = new VBox(
            new Label("Errors:"),
            errorLogArea
        );

        BorderPane upperHalfInput = new BorderPane();
        upperHalfInput.setLeft(inputBox);
        upperHalfInput.setRight(buttonBox);

        BorderPane upperHalf = new BorderPane();
        upperHalf.setLeft(upperHalfInput);
        upperHalf.setRight(errorLogAreaBox);

        VBox sendLogAreaBox = new VBox(
            new Label("Sent Messages: "),
            sendLogArea
        );
        VBox receiveLogAreaBox = new VBox(
            new Label("Received Messages: "),
            receiveLogArea
        );
        VBox logAreaBox = new VBox(
            new Label("Information: "),
            logArea
        );

        BorderPane lowerHalf = new BorderPane();
        lowerHalf.setLeft(receiveLogAreaBox);
        lowerHalf.setRight(sendLogAreaBox);
        lowerHalf.setBottom(logAreaBox);

        BordeerPane root = new BorderPane();
        root.setStyle("-fx-background-color: #658ED3;");
        root.setTop(upperHalf);
        root.setBottom(lowerHalf);
        root.setPadding(new Insets(20, 20, 20, 20));
        startButton.setOnAction(event -> startConnection());
        loadButton.setOnAction(event -> loadXmlData("config/config.xml"));

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("UDP Messaging System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void startConnection(){

        logArea.setText("");
        errorLogArea.setText("");
        sendLogArea.setText("");
        receiveLogArea.setText("");

        String udpClientPort = udpClientPortField.getText();
        String udpServerPort = udpServerPortField.getText();
        String udpServerAddress = udpServerAddressField.getText();

        if (udpClientPort.isEmpty() || udpServerPort.isEmpty() || udpServerAddress.isEmpty()) {
            log.appendText("Input Boxes cannot be empty!", "ERROR");
            return;
        }

        try{
            int clientPort = Integer.parseInt(udpClientPort);
            int serverPort = Integer.parseInt(udpServerPort);

            log("UDP Receiver started on port " + clientPort, "INFO");

            udpReceiver = new UDPReceiver(clientPort, serverPort);
            udpReceiverThread = new Thread(udpReceiver);
            udpReceiver.setFlag(true);
            udpReceiverThread.start();
            
            log("UDP Sender started on port " + serverPort, "INFO");
        catch (Exception e){
            log(e.toString(), "ERROR");
        }

        startButton.setDisable(true);
        loadButton.setDisable(true);
        stopButton.setDisable(false);

        stopButton.setOnAction(event -> {
            udpReceiver.setFlag(false);
            udpReceiver.getSocket.close();
            
            startButton.setDisable(false);
            loadButton.setDisable(false);
            stopButton.setDisable(true);
        });
    }
}
