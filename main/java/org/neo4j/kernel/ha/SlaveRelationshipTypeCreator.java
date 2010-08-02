package org.neo4j.kernel.ha;

import javax.transaction.TransactionManager;

import org.neo4j.kernel.impl.core.RelationshipTypeCreator;
import org.neo4j.kernel.impl.ha.Broker;
import org.neo4j.kernel.impl.ha.ResponseReceiver;
import org.neo4j.kernel.impl.persistence.EntityIdGenerator;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

public class SlaveRelationshipTypeCreator implements RelationshipTypeCreator
{
    private final Broker broker;
    private final ResponseReceiver receiver;

    public SlaveRelationshipTypeCreator( Broker broker, ResponseReceiver receiver )
    {
        this.broker = broker;
        this.receiver = receiver;
    }
    
    public int getOrCreate( TransactionManager txManager, EntityIdGenerator idGenerator,
            PersistenceManager persistence, String name )
    {
        return receiver.receive(
                broker.getMaster().createRelationshipType( broker.getSlaveContext(), name ) );
    }
}
