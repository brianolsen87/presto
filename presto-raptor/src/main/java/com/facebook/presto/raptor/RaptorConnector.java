/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.raptor;

import com.facebook.presto.connector.InternalConnector;
import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.ConnectorIndexResolver;
import com.facebook.presto.spi.ConnectorMetadata;
import com.facebook.presto.spi.ConnectorRecordSetProvider;
import com.facebook.presto.spi.ConnectorRecordSinkProvider;
import com.facebook.presto.spi.ConnectorSplitManager;
import com.facebook.presto.split.ConnectorDataStreamProvider;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class RaptorConnector
        implements InternalConnector
{
    private final RaptorMetadata metadata;
    private final RaptorSplitManager splitManager;
    private final RaptorDataStreamProvider dataStreamProvider;
    private final RaptorRecordSinkProvider recordSinkProvider;
    private final RaptorHandleResolver handleResolver;

    @Inject
    public RaptorConnector(
            RaptorMetadata metadata,
            RaptorSplitManager splitManager,
            RaptorDataStreamProvider dataStreamProvider,
            RaptorRecordSinkProvider recordSinkProvider,
            RaptorHandleResolver handleResolver)
    {
        this.metadata = checkNotNull(metadata, "metadata is null");
        this.splitManager = checkNotNull(splitManager, "splitManager is null");
        this.dataStreamProvider = checkNotNull(dataStreamProvider, "dataStreamProvider is null");
        this.recordSinkProvider = checkNotNull(recordSinkProvider, "recordSinkProvider is null");
        this.handleResolver = checkNotNull(handleResolver, "handleResolver is null");
    }

    @Override
    public ConnectorDataStreamProvider getDataStreamProvider()
    {
        return dataStreamProvider;
    }

    @Override
    public ConnectorHandleResolver getHandleResolver()
    {
        return handleResolver;
    }

    @Override
    public ConnectorMetadata getMetadata()
    {
        return metadata;
    }

    @Override
    public ConnectorSplitManager getSplitManager()
    {
        return splitManager;
    }

    @Override
    public ConnectorRecordSinkProvider getRecordSinkProvider()
    {
        return recordSinkProvider;
    }

    @Override
    public ConnectorRecordSetProvider getRecordSetProvider()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConnectorIndexResolver getIndexResolver()
    {
        throw new UnsupportedOperationException();
    }
}
