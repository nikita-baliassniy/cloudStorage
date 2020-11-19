package client;

import common.AbstractRequest;

public interface Callback {
    void callback(AbstractRequest request);
}
