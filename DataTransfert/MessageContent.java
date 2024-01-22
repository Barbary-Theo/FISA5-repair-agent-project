package handsOn.circularEconomy.DataTransfert;

import java.io.Serializable;

public interface MessageContent extends Serializable {

    public boolean isObjectToDisplay();
    @Override
    public String toString();

}
