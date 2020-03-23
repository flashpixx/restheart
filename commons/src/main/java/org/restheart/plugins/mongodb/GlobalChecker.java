/*-
 * ========================LICENSE_START=================================
 * restheart-commons
 * %%
 * Copyright (C) 2019 - 2020 SoftInstigate
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * =========================LICENSE_END==================================
 */
package org.restheart.plugins.mongodb;

import io.undertow.server.HttpServerExchange;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.restheart.handlers.exchange.RequestContext;
import org.restheart.handlers.exchange.RequestContextPredicate;

/**
 *
 * wraps a checker with args and confArgs to be added as a global checker
 * @deprecated use org.restheart.plugins.Interceptor instead
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class GlobalChecker {
    private final Checker checker;
    private final RequestContextPredicate predicate;
    private final boolean skipNotSupported;
    private final BsonValue args;
    private final BsonValue confArgs;

    /**
     * 
     * @param checker
     * @param predicate checker is applied only to requests that resolve
     * the predicate
     * @param skipNotSupported
     * @param args
     * @param confArgs 
     */
    public GlobalChecker(Checker checker,
            RequestContextPredicate predicate,
            boolean skipNotSupported,
            BsonValue args,
            BsonValue confArgs) {
        this.checker = checker;
        this.predicate = predicate;
        this.skipNotSupported = skipNotSupported;
        this.args = args;
        this.confArgs = confArgs;
    }

    /**
     *
     * @param exchange
     * @param context
     * @param contentToCheck
     * @return
     */
    public boolean check(
            HttpServerExchange exchange,
            RequestContext context,
            BsonDocument contentToCheck) {

        return resolve(exchange, context)
                && this.getChecker().check(exchange,
                        context,
                        contentToCheck, 
                        this.getArgs(), 
                        this.getConfArgs());
    }

    /**
     *
     * @param exchange
     * @param context
     * @return
     */
    public boolean resolve(HttpServerExchange exchange,
            RequestContext context) {
        return this.predicate.resolve(exchange, context);
    }

    /**
     *
     * @param context
     * @return
     */
    public Checker.PHASE getPhase(RequestContext context) {
        return this.getChecker().getPhase(context);
    }

    /**
     *
     * @param context
     * @return
     */
    public boolean doesSupportRequests(RequestContext context) {
        return this.getChecker().doesSupportRequests(context);
    }

    /**
     * @return the checker
     */
    public Checker getChecker() {
        return checker;
    }

    /**
     * @return the args
     */
    public BsonValue getArgs() {
        return args;
    }

    /**
     * @return the confArgs
     */
    public BsonValue getConfArgs() {
        return confArgs;
    }

    /**
     * @return the skipNotSupported
     */
    public boolean isSkipNotSupported() {
        return skipNotSupported;
    }

    /**
     * @return the predicate
     */
    public RequestContextPredicate getPredicate() {
        return predicate;
    }
}
