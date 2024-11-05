package ch.hslu.swda.micro;

import java.io.IOException;

public interface ReplenishmentClientService {
    String replenish() throws IOException, InterruptedException;
}
