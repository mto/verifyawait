package controllers;

import play.mvc.Controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author <a href="hoang281283@gmail.com">Minh Hoang TO</a>
 * @date: 6/21/13
 */
public class VerifyAwait extends Controller {

    public final static AtomicBoolean taskCompleted = new AtomicBoolean(false);

    /**
     * Redirect trick + session.getId() statement is a workaround for session id issue with await()
     */
    public static void redirectTrick()
    {
        session.getId();//Don't remove this if you don't understand it
        redirect("/index");
    }

    /**
     * To understand the root cause of session id issue:
     *
     * 1. Run app in DEV mode
     *
     * 2. Start debugger
     *
     * 3. Set a break point at play.mvc.ActionInvoker.resolve()
     *
     * 4. Browse to the link  http://localhost:9000/index   (the request goes directly to VerifyAwait.index())
     *
     * 5. play.mvc.ActionInvoker.resolve() is invoked multiple times (which is normal as Play uses Continuation for async).
     *
     * ..................
     */
    public static void index() {
        System.out.println("----------------------------START ------------------------------------");
        System.out.println("Start handling request on thread: " + Thread.currentThread().getName());
        System.out.println(session.getId());
        for (int i = 0; i < 10; i++) {
            final int index = i;
            String s = await(fetchParamTask(index));
            System.out.println("Current thread: " + Thread.currentThread().getName());
            System.out.println(index + "th result: " + s);
            System.out.println(session.getId());
        }
        System.out.println("------------------------END ------------------------------------------");

        render();
    }

    private static Future<String> fetchParamTask(final int paramIndex) {
        return new Future<String>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return taskCompleted.get();
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                taskCompleted.set(false);
                return "Task_" + paramIndex;
            }

            @Override
            public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return "Task_" + paramIndex;
            }
        };
    }

}