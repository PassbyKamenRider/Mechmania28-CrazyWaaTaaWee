package starterpack.strategy;

import javax.sql.StatementEventListener;

public enum State {
    ZD(0), RUN(1), KITE(2);

    private int numVal;

    State(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }

    public State num( int value){
        //  A(104), B(203);
        switch(value){
            case 0: return State.ZD;
            case 1: return State.RUN;
            case 2: return State.KITE;
            default:
                return State.ZD;
        }
    }
}
