import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.synth.SynthRadioButtonMenuItemUI;
import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    static Map<String, Socket> map = new HashMap<String, Socket>();

    static ServerSocket server;

    static void CommandHandler(){
        Thread command = new Thread(() -> {
            while (true){
                Scanner scanner = new Scanner(System.in);
                var input = scanner.nextLine();

                if(input.equals("users")){
                    for(String key : map.keySet()){
                        System.out.print(key + " ");
                    }
                }

            }
        });
        command.start();
    }

    static void CheckConnection() throws IOException {
        for(var s : map.keySet()){
            var client = map.get(s);

            try{
                client.getOutputStream().write(1);
            }catch(Exception e){
                System.out.println("Client " + s + " closed");
                client.close();
                map.remove(s);
            }

        }
    }

    static void SendMessages(String msg) throws IOException {
        for(var nick : map.keySet()){
            var client = map.get(nick);
            msg = nick + ": " + msg;
            var writer = new PrintWriter(client.getOutputStream(), true);
            writer.println(msg);
        }
    }

    private static volatile String input;

    static String AcceptClients() {

        Thread t = new Thread(() -> {
            Socket client = null;
            try {
                client = server.accept();
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var nick = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });

        t.start();
        /*if(map.get(nick) == null){
            map.put(nick, client);
            do{
                try {
                    input = in.readLine();
                    System.out.println(input);
                    SendMessages(input);
                } catch (Exception e) {
                }
            }while(input != null);
        }
        return null;

         */
        return "";
    }

    public static void main(String[] args) throws IOException{
        try{
            server = new ServerSocket(8080);


            CommandHandler();

            Thread mainThread = new Thread(() -> {
               while(true){
                   try {

                       CheckConnection();
                       System.out.println(" elo");
                       AcceptClients();

                   } catch (Exception e) {
                       System.out.println(e.getMessage());
                   }
               }
            });

            mainThread.start();


        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}