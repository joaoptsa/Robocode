package man;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class Movement {
    
	static final double FIELD_MARGIN = 25;
    static final double FLATTEN_CHANCE = 0.05;
    double lateral_angle = 0.2;

    AdvancedRobot bot;
    Radar radar;

    Point2D robot_location;
	
	double enemy_absbearing;
	double enemy_distance;
    Point2D enemy_location;
    

    public Movement(AdvancedRobot bot, Radar radar) {
        this.bot = bot;
        this.radar = radar;
    }

    RoundRectangle2D field(double margin) {
        return new RoundRectangle2D.Double(margin, 
                                           margin, 
                                           bot.getBattleFieldWidth() - margin * 2, 
                                           bot.getBattleFieldHeight() - margin * 2, 
                                           100 - margin, 
                                           100 - margin);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        saveEnemyPos(e);
        move();
    }

    public void saveEnemyPos(ScannedRobotEvent e) {
        robot_location = new Point2D.Double(bot.getX(),bot.getY());
		enemy_distance = e.getDistance();
		
		enemy_absbearing = bot.getHeadingRadians() + e.getBearingRadians();
		double x_offset = enemy_distance * Math.sin(enemy_absbearing);
		double y_offset = enemy_distance * Math.cos(enemy_absbearing);

		enemy_location = new Point2D.Double(bot.getX() + x_offset,bot.getY() + y_offset);
	}

    public Point2D findDestination(double tries){
        double movement_angle  = lateral_angle + absoluteBearing(enemy_location, robot_location);
        double movement_length = enemy_distance * (1.1 - tries / 100.0);

        double x_movement = movement_length * Math.sin(movement_angle);
        double y_movement = movement_length * Math.cos(movement_angle);

        double enemy_x = enemy_location.getX();
        double enemy_y = enemy_location.getY();

        Point2D destination = new Point2D.Double(enemy_x + x_movement, enemy_y + y_movement);
        return destination;
    }

    private void considerChangingDirection() {
        if (Math.random() < FLATTEN_CHANCE) {
          lateral_angle *= -1;
        }
    }

    void move() {
        considerChangingDirection();
        Point2D robot_destination = null;
        double tries = 0;
        do {
            robot_destination = findDestination(tries); //find a destination that is increasingly further away from the enemy
            tries++;                                    
        } while (tries < 100 && !field(FIELD_MARGIN).contains(robot_destination)); //if closing in on wall, close in on enemy
        goTo(robot_destination);
    }

    static double absoluteBearing(Point2D source, Point2D target) {
        return Math.atan2(target.getX() - source.getX(), 
                          target.getY() - source.getY());
    }

    private void goTo(Point2D destination) {
        double x = destination.getX();
        double y = destination.getY();
        double a;
        bot.setTurnRightRadians(Math.tan(
            a = Math.atan2(x -= (int) bot.getX(), y -= (int) bot.getY()) 
                  - bot.getHeadingRadians()));
        bot.setAhead(Math.hypot(x, y) * Math.cos(a));
    }
}






















