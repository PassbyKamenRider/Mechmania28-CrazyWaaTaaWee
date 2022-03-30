package StarterPack.action;


import StarterPack.player.Position;

public class MoveAction extends Action{
    private final Position destination;

    public MoveAction(int executingPlayerIndex, Position destination) {
        super(executingPlayerIndex);
        this.destination = destination;
    }

    public Position getPosition() {
        return destination;
    }




}
