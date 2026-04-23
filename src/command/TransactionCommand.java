package command;

/**
 * Design Pattern: Command (Command Interface)
 * Interface for all transaction commands with execute/undo capability.
 */

public interface TransactionCommand {
    void execute();
    void undo();
}
