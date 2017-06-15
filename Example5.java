import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.control.Label;
public class Example5 extends Application 
{
    public static void main(String[] args) 
    {
        launch(args);
    }

    @Override
    public void start(Stage theStage) 
    {
        theStage.setTitle( "Collect the Money Bags!" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        theStage.setScene( theScene );

        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add( canvas );
        //Creacion de array donde se guardaran los codigos obtenidos en los eventos de tecla.
        ArrayList<String> input = new ArrayList<String>();
        //Eventos de teclas. Obtencion de codigos necesarios para decidir las acciones
        theScene.setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if ( !input.contains(code) )
                        input.add( code );
                }
            });

        theScene.setOnKeyReleased(
            new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    input.remove( code );
                }
            });
        //Creacion de obejto para la renderizacion del maletin en canvas.Esta clase se utiliza para emitir llamadas de dibujo a un canvas usando un búfer.(texto superior,maletin y monedas)
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Creacion y adaptacion del cartel superior junto con la letra.
        Font theFont = Font.font( "Helvetica", FontWeight.BOLD, 24 );
        gc.setFont( theFont );
        gc.setFill( Color.GREEN );
        gc.setStroke( Color.BLACK );
        gc.setLineWidth(1);
        //Creacion del sprite del maletin(Un sprite son las caracteristicas de un obejto movil de la pantalla aisladas en una clase)y adaptacion de imagen y posicion de este;
        Sprite briefcase = new Sprite();
        briefcase.setImage("briefcase.png");
        briefcase.setPosition(200, 0);
        // Creacion de las bolsas de monedas y su colocacion.
        ArrayList<Sprite> moneybagList = new ArrayList<Sprite>();
        
        for (int i = 0; i < 15; i++)
        {
            Sprite moneybag = new Sprite();
            moneybag.setImage("moneybag.png");
            double px = 350 * Math.random() + 50;
            double py = 350 * Math.random() + 50;          
            moneybag.setPosition(px,py);
            moneybagList.add( moneybag );
        }
        //Clases para modificar los marcadores y para calcular tiempo de actualizacion
        LongValue lastNanoTime = new LongValue( System.nanoTime() );

        IntValue score = new IntValue(0);

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                // calcula el tiempo desde la ultima actualizacion
                double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                lastNanoTime.value = currentNanoTime;
                
                // Movimiento modificado para que que funcione con las teclas
                
                briefcase.setVelocity(0,0);
                if (input.contains("LEFT"))
                    briefcase.addVelocity(-50,0);
                if (input.contains("RIGHT"))
                    briefcase.addVelocity(50,0);
                if (input.contains("UP"))
                    briefcase.addVelocity(0,-50);
                if (input.contains("DOWN"))
                    briefcase.addVelocity(0,50);
                    
                briefcase.update(elapsedTime);
                
                // Recoger las bolsas de monedas
                
                Iterator<Sprite> moneybagIter = moneybagList.iterator();
                while ( moneybagIter.hasNext() )
                {
                    Sprite moneybag = moneybagIter.next();
                    if ( briefcase.intersects(moneybag) )
                    {
                        moneybagIter.remove();
                        score.value++;
                    }
                }
                // Añadido para que al finalizar desapareza el maletin y salga un mensaje de enhorabuena
                if (moneybagList.isEmpty())
                {
                    briefcase.setImage("blanco.png");
                    briefcase.addVelocity(0,0);
                    briefcase.setPosition(512,512);
                    Label label = new Label("Has ganado! Felicidades. Reinicia el juego para continuar");
                    root.getChildren().add(label);
                    label.setTranslateX(170);
                    label.setTranslateY(256);
                }
                // renderizado del maletin y del resultado, junto con actualizacion del resultado(generar la imagen del maletin y del score).
                
                gc.clearRect(0, 0, 512,512);
                briefcase.render( gc );
                
                for (Sprite moneybag : moneybagList )
                    moneybag.render( gc );

                String pointsText = "Cash: $" + (100 * score.value);
                gc.fillText( pointsText, 360, 36 );
                gc.strokeText( pointsText, 360, 36 );
            }
        }.start();

        theStage.show();
    }
}