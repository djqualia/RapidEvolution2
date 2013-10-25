package com.mixshare.rapid_evolution.net;


public class MixshareClient {
    
    private boolean is_syncing = false;
    private boolean is_out_of_sync = false;

    public void setOutOfSync(boolean is_out_of_sync) {
        this.is_out_of_sync = is_out_of_sync;
    }
    public boolean isOutOfSync() {
        return is_out_of_sync;
    }
    
    public void setIsSyncing(boolean is_syncing) {
        this.is_syncing = is_syncing;
    }
    public boolean isSyncing() {
        return is_syncing;
    }
    
}
