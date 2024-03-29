import java.util.ArrayList;
import java.util.Random;

/** Classe para objetos do tipo Exercito, um grupo de aliens dispostos em uma matriz.
* @author Heitor Tanoue de Mello - 12547260
*/
public class Exercito {
    public Alien[][] exercito;
    private Nave naveRef;

    private ArrayList<Tiro> tiros = new ArrayList<Tiro>();

    private Tuple velocidadeExercito;
    private int direcao;

    public Exercito (Nave naveRef) {
        this.naveRef = naveRef;
        this.exercito = new Alien[11][5];
        // this.numTiros = 0;
        this.direcao = 1;
        this.velocidadeExercito = new Tuple(1, 0);
        this.mobilizarExercito();
    }

    
    /** Getter para o atributo tiros.
     * @return ArrayList.Tiro - Lista de todos os tiros na tela.
     */
    public ArrayList<Tiro> getTiros() {
        return this.tiros;
    }

    
    /** Adiciona um Alien na matriz de aliens.
     * @param linha int - Linha da matriz a ser adicionado o Alien.
     * @param coluna int - Coluna da matriz a ser adicionado o Alien.
     * @param alien Alien - Alien a ser adicionado na matriz.
     */
    private void addAlien (int linha, int coluna, Alien alien) {
        this.exercito[linha][coluna] = alien;
    }

    /** Metodo que mobiliza o exercito, adicionando todos os Aliens na matriz.
     */
    public void mobilizarExercito () {
        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                int tipo;
                if ( j == 0 ) {
                    tipo = 3;
                } else if (j == 1 || j == 2) {
                    tipo = 2;
                } else {
                    tipo = 1;
                }

                this.addAlien(i, j, new Alien(tipo, new Tuple(i, j), this.velocidadeExercito));
            }
        }
    }

    
    /** Muda a velocidade do exercito para o valor passado por parametro.
     * @param v Tuple - Velocidade do exercito.
     */
    private void mudarVelocidadeExercito (Tuple v) {
        if (v.getX() > 0) {
            this.direcao = 1;
        } else if (v.getX() < 0) {
            this.direcao = -1;
        } else {
            this.direcao = 0;
        }

        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                this.exercito[i][j].setVel(v);
            }
        }

        this.velocidadeExercito = v;
    }

    /** Acelera o exercito na direcao atual.
     */
    public void acelerarExercito () {
        Tuple novaVelocidade = this.velocidadeExercito;
        novaVelocidade.deslocar(0.5 * this.direcao, 0);
        mudarVelocidadeExercito(novaVelocidade);
    }

    /** Move o Exercito de acordo com sua velocidade atual, detecta com as colisoes com borda e anda uma unidade para baixo caso colida.
     */
    public void moverExercito () {
        if (this.direcao == -1 && this.exercito[0][0].colisaoTela() ||
        this.direcao == 1 && this.exercito[this.exercito.length - 1][0].colisaoTela()) {
            Tuple velAtual = this.velocidadeExercito;
            velAtual.setX(velAtual.getX() * -1);
            this.mudarVelocidadeExercito(new Tuple(0, 1));
            this.moverExercito();
            this.mudarVelocidadeExercito(velAtual);
        }

        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                this.exercito[i][j].deslocar();
            }
        }
    }

    
    /** Faz com que o Exercito atire, um tiro guiado na posicao do jogador (caso esse esteja alinhado com o Exercito) e outro de uma coluna randomica.
     * @param c Console - Console para desenhar os Tiros.
     */
    public void atirar ( Console c ) {
        if (tiros.size() >= 2) {
            return;
        }

        // tiro randomico
        Random rand = new Random();
        int randLinha = rand.nextInt(this.exercito.length - 1);
        for (int i = this.exercito[0].length - 1; i >= 0; i--) {
            this.tiros.add(this.exercito[randLinha][i].atirar());
            break;
        }

        // checa se a nave esta alinhada com alguma coluna do exercito
        int xNave = (int) this.naveRef.getPos().getX();
        if (this.exercito[0][0].getPos().getX() > xNave ||
            this.exercito[this.exercito.length - 1][0].getPos().getX() < xNave) {
            return;
        }

        // tiro focado na nave
        int xRelative = (int) (xNave - this.exercito[0][0].getPos().getX());
        for (int j = this.exercito[0].length - 1; j >= 0; j--) {
            if (this.exercito[xRelative][j].getVivo()) {
                this.tiros.add(this.exercito[xRelative][j].atirar());
                // System.out.println("tiros[0] = " + this.tiros.get(0).getPos().toString());
                break;
            }
        }
    }

    
    /** Retorna o numero de Aliens vivos no Exercito.
     * @return int
     */
    public int numAliensVivos () {
        int numAliensVivos = 0;
        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                if (this.exercito[i][j].getVivo()) {
                    numAliensVivos++;
                }
            }
        }
        return numAliensVivos;
    }

    
    /** Retorna o Alien que colidiu com o Tiro passado por parametro.
     * @param t Tiro - Tiro que colidiu com algum Alien.
     * @return Alien / null - Alien que colidiu com o Tiro.
     */
    public Alien alienColisao ( Tiro t ) {
        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                if (this.exercito[i][j].colisaoEntidade(t)) {
                    return this.exercito[i][j];
                }
            }
        }
        return null;
    }

    
    /** Coloca o conteudo do Exercito no Console passado por parametro.
     * @param c Console - Console para desenhar o Exercito.
     */
    public void imprimir ( Console c ) {
        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                if (this.exercito[i][j].getVivo()) {
                    this.exercito[i][j].imprimir(c);
                }
            }
        }
    }

    
    /** Coloca o conteudo dos Tiros do Exercito no Console passado por parametro.
     * @param c Console - Console para desenhar os Tiros.
     */
    public void imprimirTiros ( Console c ) {
        for (int i = 0; i < this.tiros.size(); i++) {
            Tiro tiroAtual = this.tiros.get(i);
            if (tiroAtual == null || !tiroAtual.getVisivel() || tiroAtual.colisaoTela()) {
                this.tiros.remove(i);
                i--;
            } else {
                tiroAtual.imprimir(c);
            }
        }
    }

    /** [CHEAT] Mata todos os Aliens do Exercito.
     */
    public void matarExercito () {
        for (int i = 0; i < this.exercito.length; i++) {
            for (int j = 0; j < this.exercito[i].length; j++) {
                this.exercito[i][j].setVivo(false);
            }
        }
    }

    
    /** Retorna a altura (y) do Alien vivo mais abaixo na tela.
     * @return int
     */
    public int getAlturaUltimoAlienVivo () {
        for (int i = this.exercito[0].length - 1; i >= 0; i--) {
            for (int j = 0; j < this.exercito.length; j++) {
                if (this.exercito[j][i].getVivo()) {
                    return (int) this.exercito[j][i].getPos().getY();
                }
            }
        }
        return 0;
    }
}
