package niteknightt.gameplay;

import java.util.Stack;

public class GameHistory {
    
    public void addMoveProperties(MoveProperties moveProperties) {
        _moveProperties.push(moveProperties);
    }

    public void undoLastMove() {
       _moveProperties.pop();
    }

    public boolean isEmpty() {
        return _moveProperties.empty();
    }

    public void clear() {
        while (!_moveProperties.empty())
            _moveProperties.pop();
    }

    public GameHistory clone() {
        GameHistory clone = new GameHistory();
        //std::shared_ptr<GameHistory> clone = std::make_shared<GameHistory>();
        clone.setMoveProperties((Stack<MoveProperties>)_moveProperties.clone());
        return clone;
    }

    // Methods for clone
    public Stack<MoveProperties> getMoveProperties() { return _moveProperties; }
    public void setMoveProperties(Stack<MoveProperties> val) { _moveProperties = val; }
    public MoveProperties lastMoveProperties() { return _moveProperties.peek(); }

    public static void copy_reverse(Stack<String> source, Stack<String> dest) {

    }

    public static void CopyStack(Stack<String> source, Stack<String> target) {

    }

    protected Stack<MoveProperties> _moveProperties = new Stack<MoveProperties>();

}
