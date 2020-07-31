package man;
import java.awt.geom.*;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.*;
import robocode.util.Utils;


public class Gun {
    
	ScannedRobotEvent enemy;
	AdvancedRobot bot;
    double firePower;
    double old;
    double delta=0;
    
    
    public Gun(AdvancedRobot bot) {
        this.bot = bot;
           }

    public void onScannedRobot(ScannedRobotEvent e) {
      this.enemy=e;
      doGun(enemy);
      }
    
    
    //   Escolher a  força do tiro: 
    //   Em 1v1, tiro de força 2.0;
    //   Quando o inimigo está muito distante, reduz a força; 
    //   Quando muito próximo, aumenta a força;
    //   Quando a própria energia estiver baixa, reduz a força;
    
    
   public double decideFire (ScannedRobotEvent e){
      
	 firePower = 2.0; 
     
     if(e.getDistance()>400)
       firePower=1.0;
     else
     if(e.getDistance()<100)
       firePower=3.0; 
     
     if(bot.getEnergy()<1)
       firePower=0.1;
     else
     if(bot.getEnergy()<10)
      firePower=1.0;
   
     return Math.min(e.getEnergy()/4,firePower);
  }

 
   
   /*
    * Método Circular Targeting
    * 
    */
   
 public void doGun(ScannedRobotEvent e) {
	 
	 firePower = decideFire(e);
	 Rules.getBulletSpeed(firePower);
	 
	 //Coordenadas do robô
	 double X =bot.getX();
	 double Y =bot.getY();
	 
	 //Ângulo absoluto ao inimigo
	 double absangle = bot.getHeadingRadians() + e.getBearingRadians();
	 
	 //A direção do inimigo e a  sua alteração
	 double Heading_enemy = e.getHeadingRadians();
	 double HeadingChange_enemy = Heading_enemy - old;
	 old = Heading_enemy;
	 
	 //Coordenadas do inimigo 
	 double predX= bot.getX()+e.getDistance()*Math.sin(absangle);
	 double predY =bot.getY()+e.getDistance()*Math.cos(absangle);
	 
	 //Distância entre as coordenadas do robo e do seu inimigo
	 double distance =Point2D.Double.distance(X, Y, predX, predY);
	
	 
	 
	 while((delta) * (20.0 - 3.0 * firePower) <  distance){   
     
     //Adiciona o movimento que achamos que nosso inimigo fará aos atuais X e Y
     predX += Math.sin(Heading_enemy) * e.getVelocity();
     predY += Math.cos(Heading_enemy) * e.getVelocity();
     
    //Procura as mudanças de rumo do inimigo.
     Heading_enemy += HeadingChange_enemy;
    
     //Se coordenadas previstas estiverem fora das paredes,coloca a 18 unidades de distância das paredes
     predX=Math.max(Math.min(predX,bot.getBattleFieldWidth()-18),18);
     predY=Math.max(Math.min(predY,bot.getBattleFieldHeight()-18),18);
     
     distance=Point2D.Double.distance(X, Y, predX, predY);
     ++delta;
     }
	 //Encontra o rumo das nossas coordenadas previstas por nós.
     double mtr = Utils.normalAbsoluteAngle(Math.atan2( predX - X, predY - Y));
     
     //Objetivo e fogo.
     bot.setTurnGunRightRadians(Utils.normalRelativeAngle(mtr - bot.getGunHeadingRadians()));
	 bot.setFire(firePower);
	 
	 //Define o radar do robô para virar à direita por  x radianos quando a próxima execução ocorrer.
	 bot.setTurnRadarRightRadians(Utils.normalRelativeAngle(absangle-bot.getRadarHeadingRadians())*2);
 
 }
 
 
}

