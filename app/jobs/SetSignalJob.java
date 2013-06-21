package jobs;

import controllers.VerifyAwait;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 6/21/13
 */
@OnApplicationStart
public class SetSignalJob extends Job {

    @Override
    public void doJob() throws Exception
    {
        Thread setSignal = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        VerifyAwait.taskCompleted.set(true);
                    } catch (InterruptedException iex) {
                        iex.printStackTrace();
                    }
                }
            }
        };
        setSignal.setDaemon(true);
        setSignal.start();

    }
}
