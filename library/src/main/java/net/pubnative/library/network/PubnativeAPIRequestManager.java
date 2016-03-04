// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PubnativeAPIRequestManager {

    private static final String                         TAG                               = PubnativeAPIRequestManager.class.getSimpleName();
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int                            KEEP_ALIVE_TIME                   = 1;
    private static final TimeUnit                       KEEP_ALIVE_TIME_UNIT              = TimeUnit.SECONDS;
    // Sets the Thread pool size
    private static final int                            CORE_POOL_SIZE                    = 8;
    private static final int                            MAXIMUM_POOL_SIZE                 = 8;
    // A queue of Runnables for requests pool
    private              BlockingQueue<Runnable>        mRequestWorkQueue                 = null;
    private              ThreadPoolExecutor             mRequestThreadPool                = null;
    private              Queue<PubnativeAPIRequestTask> mPubnativeAPIRequestTaskWorkQueue = null;
    //==============================================================================================
    // THREAD FACTORY
    //==============================================================================================
    private static       ThreadFactory                  sThreadFactory                    = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {

            return new Thread(r, "PubnativeAPIRequest #" + mCount.getAndIncrement());
        }
    };

    //==============================================================================================
    // SINGLETON
    //==============================================================================================
    private PubnativeAPIRequestManager() {
        // Creates a work queue for the pool of Thread objects used for executing requests, using a linked
        // list queue that blocks when the queue is empty.
        mRequestWorkQueue = new LinkedBlockingQueue<Runnable>();
        // Creates a work queue for the set of of task objects that control executing requests,
        // using a linked list queue that blocks when the queue is empty.
        mPubnativeAPIRequestTaskWorkQueue = new LinkedBlockingQueue<PubnativeAPIRequestTask>();
        //Creates a new pool of Thread objects for executing requests queue
        mRequestThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                                                    KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mRequestWorkQueue, sThreadFactory);
    }

    private static PubnativeAPIRequestManager sInstance;

    private static PubnativeAPIRequestManager getInstance() {

        if (sInstance == null) {
            sInstance = new PubnativeAPIRequestManager();
        }
        return sInstance;
    }

    //==============================================================================================
    // Public
    //==============================================================================================
    public static void sendRequest(PubnativeAPIRequest pubnativeAPIRequest) {

        Log.v(TAG, "sendRequest");
        // Gets a task from the pool of tasks, returning null if the pool is empty
        PubnativeAPIRequestTask pubnativeAPIRequestTask = getInstance().mPubnativeAPIRequestTaskWorkQueue.poll();
        // If the queue was empty, create a new task instead.
        if (null == pubnativeAPIRequestTask) {
            pubnativeAPIRequestTask = new PubnativeAPIRequestTask();
        }
        pubnativeAPIRequestTask.setRequest(pubnativeAPIRequest);
        final Handler handler = new Handler(Looper.getMainLooper());
        pubnativeAPIRequestTask.setResponsePoster(new Executor() {

            @Override
            public void execute(Runnable runnable) {

                handler.post(runnable);
            }
        });
        getInstance().mRequestThreadPool.execute(pubnativeAPIRequestTask.getExecuteRequestRunnable());
    }

    /**
     * Recycles tasks by calling their internal recycle() method and then putting them back into
     * the task queue.
     *
     * @param pubnativeAPIRequestTask The task to recycle
     */
    public static void recycleTask(PubnativeAPIRequestTask pubnativeAPIRequestTask) {

        Log.v(TAG, "recycleTask");
        // Puts the task object back into the queue for re-use.
        getInstance().mPubnativeAPIRequestTaskWorkQueue.offer(pubnativeAPIRequestTask);
    }
}
