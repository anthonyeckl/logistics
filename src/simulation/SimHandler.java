package simulation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class SimHandler {
    // extra list for all the robots
    LinkedList<Robot> robot = new LinkedList<>();
    // extra list for all the tiles
    ArrayList<Tile> tiles = new ArrayList<>();

    private void checkCollision(Robot r1, Robot r2) {
        Robot stationary;
        Robot moving;
        boolean directCollision = false;
        // two moving robots
        if(Math.abs(r1.getDirection() - r2.getDirection()) == 2 && r1.getDirection() != -1 && r2.getDirection() != -1 && r1.getPathTiles().size() != 0 && r2.getPathTiles().size() != 0 ){
                System.out.println("DIRECT Collision!!! ");
                directCollision = true;
        }
        if (r1.getPathTiles().size() != 0 && r2.getPathTiles().size() != 0 && !directCollision) {

            // ci/cj -> number to which Tile they will be compared
            int ci = 3;
            int cj = 3;
            // if the tile list is shorter than 3, the length of the list will be taken
            if (r1.getPathTiles().size() <= 3)
                ci = r1.getPathTiles().size();
            if (r2.getPathTiles().size() <= 3)
                cj = r2.getPathTiles().size();
            // if both robots will end up on the same tile at the same time, the collision will be triggered
            for (int i = 0; i < ci; i++) {
                for (int j = 0; j < cj; j++) {
                    if (Arrays.equals(r1.getPathTiles().get(i), r2.getPathTiles().get(j)) && i == j) {
                        System.out.println("Collision!!! ");
                        r1.waitRobot();
                    }
                }
            }
            // one idle one moving
        } if (r1.getPathTiles().size() != 0 && r2.getPathTiles().size() == 0 || r1.getPathTiles().size() == 0 && r2.getPathTiles().size() != 0 || directCollision) {
            // identifying which of the robots is idle
            if (r1.getPathTiles().size() == 0) {
                stationary = r1;
                moving = r2;
            } else {
                stationary = r2;
                moving = r1;
            }
            int[] stationaryCords = new int[2];
            stationaryCords[0] = stationary.getX();
            stationaryCords[1] = stationary.getY();
            int iterationNumber = 3;
            // limiting the iterationNumber at the list length
            if(moving.getPathTiles().size() < iterationNumber)
                iterationNumber = moving.getPathToTarget().size();
            // Colission triggered when idle robot on path of moving robot
            for (int i = 0; i < iterationNumber; i++) {
                if (Arrays.equals(moving.getPathTiles().get(i), stationaryCords)) {
                    System.out.println("Collision with Idle!!! ");
                    moving.obstacleOnPath(i);
                }

            }
        }

    }

    public void tick() {
        // calculates distance between every Robot to every other Robot
        for (int i = 0; i < robot.size(); i++) {
            Robot tempRobot = robot.get(i);
            tempRobot.tick();
            for (int j = i + 1; j < robot.size(); j++) {
                Robot tempRobot2 = robot.get(j);
                double distance;
                distance = Math.sqrt((tempRobot2.getY() - tempRobot.getY()) * (tempRobot2.getY() - tempRobot.getY()) + (tempRobot2.getX() - tempRobot.getX()) * (tempRobot2.getX() - tempRobot.getX()));
                // if the distance is less than 40px the collision avoidance gets activated
                if (distance < 40) {
                    //System.out.println("Collision Danger: " + tempRobot.getNumber() + ", " + tempRobot2.getNumber());
                    checkCollision(tempRobot, tempRobot2);
                }

            }


        }
    }

    public void render(Graphics g) {

        for (int i = 0; i < robot.size(); i++) {
            Robot tempRobot = robot.get(i);
            tempRobot.render(g);
        }

    }

    // GETTERS AND SETTERS
    public void addRobot(Robot addrobot) {
        this.robot.add(addrobot);
    }

    public void removeRobot(Robot remrobot) {
        this.robot.remove(remrobot);
    }

    public void addTile(Tile tile) {
        this.tiles.add(tile);
    }

    public void removeTile(Tile tile) {
        this.tiles.remove(tile);
    }

    public LinkedList<Robot> getRobot() {
        return robot;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

}
