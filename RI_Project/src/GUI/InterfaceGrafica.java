package GUI;
 
import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
 
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
 
import processor.Game;
 
 
public class InterfaceGrafica extends JFrame {
 
    private JPanel panel;
    private JPanel infoPanel;
    private JScrollPane infoScrollPanel;
    private JTextPane textInfo;
    private String precos[];   
 
    private JLabel lblTitulo;
    private JLabel lblPlataforma;
    private JLabel lblPreco;
    private JLabel lblDesenvolvedor;
    private JLabel lblGenero;
 
    private JTextField fieldTitulo;
    private JTextField fieldPlataforma;
    private String fieldPreco = "";
    private JTextField fieldDesenvolvedor;
    private JTextField fieldGenero;
    private static Fachada fachada;
 
    private JButton btnPesquisar;
 
    public InterfaceGrafica(){
        setTitle("SuaBusca");
        setAutoRequestFocus(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 629, 597);
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panel);
        panel.setLayout(null);
 
        int first = 40;
        int second = 80;
        int third = 120;
        int fourth = 160;
 
        String[] precos = { " --- ", "Menos de " + first, "Entre " + first + " e " + second,"Entre " + second + " e " + third,"Entre " + third + " e " + fourth, "Mais de " + fourth };
 
        JLabel text = new JLabel("Adicione as Informações que deseja buscar, abaixo.");
        text.setBounds(115, 20, 300, 15);
 
        lblTitulo = new JLabel("Titulo: ");
        lblTitulo.setBounds(80, 50, 90, 15);
 
        lblPlataforma = new JLabel("Plataforma: ");
        lblPlataforma.setBounds(80,90,90,15);
 
        lblPreco = new JLabel("Preço: ");
        lblPreco.setBounds(80, 130, 90, 15);
 
        lblDesenvolvedor = new JLabel("Desenvolvedor: ");
        lblDesenvolvedor.setBounds(80, 170, 90, 15);
 
        lblGenero = new JLabel("Gênero: ");
        lblGenero.setBounds(80, 210,90,15);
 
        panel.add(text);
        panel.add(lblTitulo);
        panel.add(lblPlataforma);
        panel.add(lblPreco);
        panel.add(lblDesenvolvedor);
        panel.add(lblGenero);
 
        fieldTitulo = new JTextField();
        fieldTitulo.setBounds(190, 50, 170, 20);
 
        fieldPlataforma = new JTextField();
        fieldPlataforma.setBounds(190, 90, 170, 20);
 
        JComboBox<?> comboBoxPreco = new JComboBox<Object>(precos);
        comboBoxPreco.setBounds(190, 130, 170, 20);
 
        fieldDesenvolvedor = new JTextField();
        fieldDesenvolvedor.setBounds(190, 170, 170, 20);
 
        fieldGenero = new JTextField();
        fieldGenero.setBounds(190, 210, 170, 20);
 
        panel.add(fieldTitulo);
        panel.add(fieldPlataforma);
        panel.add(comboBoxPreco);
        panel.add(fieldDesenvolvedor);
        panel.add(fieldGenero);
 
        fieldTitulo.setColumns(15);
        fieldPlataforma.setColumns(15);
        fieldDesenvolvedor.setColumns(15);
        fieldGenero.setColumns(15);
 
        btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.setBounds(210,240,125,25);
        panel.add(btnPesquisar);
 
 
        comboBoxPreco.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox<?> cb = (JComboBox<?>) e.getSource();
                String value = (String) cb.getSelectedItem();
 
                if (value.equals("Menos de " + first)) {
                    fieldPreco = "_40";
                } else if (value.equals("Entre " + first + " e " + second)) {
                    fieldPreco = "40_80";
                } else if (value.equals("Entre " + second + " e " + third)) {
                    fieldPreco = "80_120";
                } else if (value.equals("Entre " + third + " e " + fourth)) {
                    fieldPreco = "120_160";
                } else if(value.equals("Mais de " + fourth)){
                    fieldPreco = "160_";
                }else{
                    fieldPreco = "";
                }
            }
        });
 
        // pesquisar.addAction
 
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBounds(40, 310, 540, 220);
        panel.add(infoPanel);
 
        JLabel textLabel = new JLabel("Resultado da Busca:");
        textLabel.setBounds(80, 280, 300, 15);
        panel.add(textLabel);
 
        textInfo = new JTextPane();
        textInfo.setEditable(false);
 
        infoScrollPanel = new JScrollPane(textInfo,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        infoPanel.add(infoScrollPanel);    
 
        StringBuffer result = new StringBuffer();
 
        btnPesquisar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fachada.searchGame(fieldTitulo.getText(), fieldPlataforma.getText(), fieldPreco, fieldDesenvolvedor.getText(), fieldGenero.getText());
                List<Game> games = fachada.getList();
 
                if(games.size() != 0){
                    for (Game game : games) {
 
 
                        if((game.getTitulo()==null) || game.getTitulo().equals("")) game.setTitulo("Titulo Indisponível.");
                        if((game.getPlataforma()==null) || game.getPlataforma().equals("")) game.setPlataforma("Plataforma Indisponível.");
                        if((game.getPreco()==null) || game.getPreco().equals("")) game.setPreco("Preço Indisponível.");
                        if((game.getDesenvolvedor()==null) || game.getDesenvolvedor().equals("")) game.setDesenvolvedor("Desenvolvedor Indisponível.");
                        if((game.getGenero()==null) || game.getGenero().equals("")) game.setGenero("Gênero Indisponível.");
                        if((game.getUrl()==null) || game.getUrl().equals("")) game.setUrl("Url Indisponível.");
 
                        result.append("Título: " + game.getTitulo() + "\n");
                        result.append("Plataforma: " + game.getPlataforma() + "\n");
                        result.append("Preço: " + game.getPreco() + "\n");            
                        result.append("Desenvolvedor: " + game.getDesenvolvedor() + "\n"); 
                        result.append("Gênero: " + game.getGenero() + "\n");        
                        result.append("Url: " + game.getUrl());
                        result.append("\n\n");
 
                    }
                }else{
                    result.append("Nenhum resultado encontrado.");
                }
                textInfo.setText(result.toString());
            }
        });
       
        JLabel lblTitulo1 = new JLabel("Ex: Mario, Batman e Call of Duty.");
        lblTitulo1.setBounds(380, 50, 200, 15);
       
        JLabel lblPlataforma1 = new JLabel("Ex: PC, PS4, PS3.");
        lblPlataforma1.setBounds(380, 90, 200, 15);
       
        JLabel lblPreco1 = new JLabel("Ex: [-40], [80-120], [120-160].");
        lblPreco1.setBounds(380, 130, 200, 15);
       
        JLabel lblDesenvolvedor1 = new JLabel("Ex: Paradoxo, SCS Software, Zynga.");
        lblDesenvolvedor1.setBounds(380, 170, 220, 15);
       
        JLabel lblGenero1 = new JLabel("Ex: Aventura, Estratégia, Simulador.");
        lblGenero1.setBounds(380, 210, 220, 15);
       
        panel.add(lblTitulo1);
        panel.add(lblPlataforma1);
        panel.add(lblPreco1);
        panel.add(lblDesenvolvedor1);
        panel.add(lblGenero1);
 
    }
 
    public static void main(String[] args) {
        fachada = new Fachada();
 
        InterfaceGrafica frame = new InterfaceGrafica();
        frame.setVisible(true);
    }
}