package simulation;

import simulation.Data.DBHandler;

public class DistributionThread extends Thread{
    
    private DBHandler dbhandler;
    
    public DistributionThread setDBhandler(DBHandler dbhandler){
        this.dbhandler = dbhandler;
        return this;
    }
    public void run(){
        DistributionWindow dWindow = new DistributionWindow(this.dbhandler);
        boolean active = true;

        while(active){
            try {
                dWindow.dWindowRender();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                active = false;
            }
        }
    }

}
