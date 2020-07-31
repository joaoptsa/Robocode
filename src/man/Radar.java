package man;
import robocode.*;
import robocode.AdvancedRobot;
import robocode.util.Utils;


public class Radar {
    AdvancedRobot bot;
    ScannedRobotEvent enemy;

    public Radar(AdvancedRobot bot) {
        this.bot = bot;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
		this.enemy = e;
        widthLock(enemy);
	}

    public void widthLock(ScannedRobotEvent e) {
		//angulo absoluto ao inimigo
		double enemy_absangle = bot.getHeadingRadians() + e.getBearingRadians();
		//calcular rotação do radar necessária para se virar para o inimigo, normalizada
		double radar_turn = Utils.normalRelativeAngle(enemy_absangle - bot.getRadarHeadingRadians() );
		// prevenção de overshooting. 36 = num de unidades a partir do centro do inimigo.
		double slip_compensation = Math.min( Math.atan( 25.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );

		//principio de slip_compensation
		//se radar virar à esquerda, queremos que vire mais um pouco à esquerda
		// análogo para direita
		if(radar_turn < 0)
			radar_turn -= slip_compensation;
		else	
			radar_turn += slip_compensation;

		bot.setTurnRadarRightRadians(radar_turn);
    } 
    
 }