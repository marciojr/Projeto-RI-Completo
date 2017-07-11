package GUI;
 
import java.util.List;
 
import processor.DataProcessor;
import processor.Game;
 
public class Fachada {
   
    private Buscador buscador;
   
    public Fachada(){
       
    }
   
    public void searchGame(String titulo, String plataforma, String preco, String desenvolvedor, String genero){
        buscador = new Buscador(titulo,plataforma,preco,desenvolvedor,genero);
        buscador.limpaBusca(titulo,plataforma,preco,desenvolvedor,genero);
    }
   
    public List<Game> getList(){
        DataProcessor data = new DataProcessor();
        return data.getRankedGames(buscador.getKeys());
    }
   
}