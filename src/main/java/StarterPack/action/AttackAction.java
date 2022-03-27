package StarterPack.action;

public class AttackAction extends Action {

    private int target;

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public AttackAction() {}

    public AttackAction(int executingPlayerIndex, int target) {
        super(executingPlayerIndex);
        this.target = target;
    }

}
