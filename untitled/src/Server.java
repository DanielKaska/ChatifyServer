import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    public static void main(String[] args) throws IOException{
        try{
            ServerSocket server = new ServerSocket(8080);
            Map<String, Socket> map = new HashMap<String, Socket>();

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

            while(true){
                Socket client = server.accept();

                Thread t = new Thread(() -> {
                    try{
                        var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        var out = new PrintWriter(client.getOutputStream(), true);

                        var nick = in.readLine();

                        if(map.get(nick) == null){
                            map.put(nick, client);
                            System.out.println(map.get(nick));
                            String input;

                            while((input = in.readLine()) != null){
                                out.println(nick + ": " + input);
                            }
                        }else{
                            out.println("Nickname is already in use");
                        }


                    }catch(Exception e){
                            try {
                                client.close();
                                map.remove(client);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                });

                t.start();

            }


        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}