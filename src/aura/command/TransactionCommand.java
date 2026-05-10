package aura.command;

import aura.domain.TransactionResult;

public interface TransactionCommand {
    TransactionResult execute();
}
