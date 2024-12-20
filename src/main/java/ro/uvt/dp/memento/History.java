package ro.uvt.dp.memento;

import java.util.Stack;

public class History {
    private final Stack<AccountMemento> mementos = new Stack<>();

    public void saveState(AccountMemento memento) {
        mementos.push(memento);
    }

    public AccountMemento getLastSavedState() {
        if (!mementos.isEmpty()) {
            return mementos.pop();
        }
        return null;
    }
}
