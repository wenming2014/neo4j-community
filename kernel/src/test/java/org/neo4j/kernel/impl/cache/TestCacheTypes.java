/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.cache;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.AbstractNeo4jTestCase;
import org.neo4j.kernel.impl.core.NodeManager.CacheType;

public class TestCacheTypes extends AbstractNeo4jTestCase
{
    private static final String PATH = getStorePath( "cache-db" );

    @BeforeClass
    public static void clear()
    {
        deleteFileOrDirectory( new File( PATH ) );
    }

    private GraphDatabaseService newDb( String cacheType )
    {
        return newDb( cacheType, MapUtil.stringMap() );
    }

    private GraphDatabaseService newDb( String cacheType, Map<String, String> config )
    {
        return new EmbeddedGraphDatabase( PATH, MapUtil.stringMap( config, Config.CACHE_TYPE, cacheType ) );
    }

    @Test
    public void testDefaultCache()
    {
        GraphDatabaseService db = newDb( null );
        try
        {
            assertEquals( CacheType.soft,
                    ( (AbstractGraphDatabase) db ).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        }
        finally
        {
            db.shutdown();
        }
    }

    @Test
    public void testWeakRefCache()
    {
        GraphDatabaseService db = newDb( "weak" );
        assertEquals( CacheType.weak, ((EmbeddedGraphDatabase) db).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        db.shutdown();
    }

    @Test
    public void testSoftRefCache()
    {
        GraphDatabaseService db = newDb( "soft" );
        assertEquals( CacheType.soft, ((EmbeddedGraphDatabase) db).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        db.shutdown();
    }

    @Test
    public void testNoCache()
    {
        GraphDatabaseService db = newDb( "none" );
        assertEquals( CacheType.none, ((EmbeddedGraphDatabase) db).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        db.shutdown();
    }

    @Test
    public void testStrongCache()
    {
        GraphDatabaseService db = newDb( "strong" );
        assertEquals( CacheType.strong, ((EmbeddedGraphDatabase) db).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        db.shutdown();
    }

    @Test
    public void testGcrCache()
    {
        GraphDatabaseService db = newDb( "gcr" );
        assertEquals( CacheType.gcr,
                ( (AbstractGraphDatabase) db ).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        db.shutdown();
    }

    @Test
    public void testGcrCacheWithNodeSizeConfig()
    {
        GraphDatabaseService db = newDb( "gcr", MapUtil.stringMap( Config.NODE_CACHE_SIZE, "100M" ) );
        assertEquals( CacheType.gcr,
                ( (AbstractGraphDatabase) db ).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
        // TODO how to assert that 100M is actually used
        db.shutdown();
    }

//    @Test
//    public void testOldCache()
//    {
//        GraphDatabaseService db = newDb( "old" );
//        assertEquals( CacheType.old, ((EmbeddedGraphDatabase) db).getConfig().getGraphDbModule().getNodeManager().getCacheType() );
//        db.shutdown();
//    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidCache()
    {
        // invalid cache type should use default and print a warning
        newDb( "whatever" );
    }
}