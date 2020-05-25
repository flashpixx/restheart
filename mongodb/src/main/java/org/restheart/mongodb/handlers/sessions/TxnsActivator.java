/*
 * Copyright SoftInstigate srl. All Rights Reserved.
 *
 *
 * The copyright to the computer program(s) herein is the property of
 * SoftInstigate srl, Italy. The program(s) may be used and/or copied only
 * with the written permission of SoftInstigate srl or in accordance with the
 * terms and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied. This copyright notice must not be removed.
 */
package org.restheart.mongodb.handlers.sessions;

import org.restheart.exchange.ExchangeKeys.METHOD;
import org.restheart.exchange.ExchangeKeys.TYPE;
import org.restheart.mongodb.db.MongoClientSingleton;
import org.restheart.mongodb.db.sessions.TxnClientSessionFactory;
import org.restheart.mongodb.handlers.RequestDispatcherHandler;
import org.restheart.mongodb.handlers.injectors.ClientSessionInjector;
import org.restheart.mongodb.utils.LogUtils;
import org.restheart.plugins.Initializer;
import org.restheart.plugins.RegisterPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
@RegisterPlugin(name = "txnsActivator",
        description = "activates support for transactions",
        priority = Integer.MIN_VALUE + 1)
public class TxnsActivator implements Initializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TxnsActivator.class);

    @Override
    public void init() {
        if (!MongoClientSingleton.getInstance().isReplicaSet()) {
            LogUtils.boxedWarn(LOGGER,
                    "MongoDB is a standalone instance.",
                    "",
                    "Transactions require a Replica Set.",
                    "",
                    "More information at https://restheart.org/docs/setup/");
        } else {
            enableTxns();
        }
    }

    private void enableTxns() {
        var dispatcher = RequestDispatcherHandler.getInstance();

        ClientSessionInjector.getInstance()
                .setClientSessionFactory(TxnClientSessionFactory
                        .getInstance());

        // *** Txns handlers
        dispatcher.putHandler(TYPE.TRANSACTIONS, METHOD.POST,
                new PostTxnsHandler());

        dispatcher.putHandler(TYPE.TRANSACTIONS, METHOD.GET,
                new GetTxnHandler());

        dispatcher.putHandler(TYPE.TRANSACTION, METHOD.DELETE,
                new DeleteTxnHandler());

        dispatcher.putHandler(TYPE.TRANSACTION, METHOD.PATCH,
                new PatchTxnHandler());
    }
}