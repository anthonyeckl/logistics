package simulation;

public class Job {
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;
    private int targetBoxID;
    private int[] transitRoute;
    private int[] transportRoute;
    private Robot jobRobot;
    private Organiser organiser;

    private int orderID = -1;

    public Job(int[] jobCords, Robot jobRobot, Organiser organiser){
        fromX = jobCords[0];
        fromY = jobCords[1];
        toX = jobCords[2];
        toY = jobCords[3];
        targetBoxID = jobCords[4];
        // in case the Job doesnt originate from an order, but from an movement
        if(jobCords.length>5)
            orderID = jobCords[5];
        this.jobRobot = jobRobot;
        this.organiser = organiser;

        // if the robot is not already on the from location
        if(fromX != jobRobot.getX() && fromY != jobRobot.getY()){
            // two different routes
            transitRoute = new int[]{fromX, fromY, jobRobot.getX(), jobRobot.getY()};
            transportRoute = new int[]{toX, toY, fromX, fromY};
            // beginn with transit
            jobRobot.newRoute(transitRoute[0],transitRoute[1],transitRoute[2],transitRoute[3]);
            jobRobot.setTaskTransit();
        }else{
            // if the robot already is on the from location it directly starts with the transport
            jobRobot.newRoute(toX, toY, jobRobot.getX(), jobRobot.getY());
            jobRobot.setTaskTransport();
            if(targetBoxID != -1)
                boxPickUp();
        }
    }

    public void reachedEndpoint(){
        // depending on what the task was, the following actions are initiated
        if(jobRobot.getTask().equals("TRANSPORT")){
            jobRobot.idleMode();
            if(targetBoxID != -1){
                boxDrop();
                if(orderID != -1){
                    organiser.watchCollectorTile(orderID);
                    organiser.jobFromOrderFinished(orderID, targetBoxID);
                }
            }

        } else if(jobRobot.getTask().equals("TRANSIT")){
            jobRobot.setTaskTransport();
            jobRobot.newRoute(transportRoute[0],transportRoute[1],transportRoute[2],transportRoute[3]);
            boxPickUp();
        }
    }

    public void boxPickUp(){
        //remove from database
        jobRobot.getDbHandler().updateTilebyBoxID(Sim.calcTileNum(fromX,fromY), targetBoxID, "remove");
        jobRobot.getDbHandler().updateBoxbyTileID(targetBoxID, -1);
    }

    public void boxDrop(){
        //update/add to database
        //remove from database
        jobRobot.getDbHandler().updateTilebyBoxID(Sim.calcTileNum(toX,toY), targetBoxID, "add");
        jobRobot.getDbHandler().updateBoxbyTileID(targetBoxID, Sim.calcTileNum(toX,toY));
    }

}
