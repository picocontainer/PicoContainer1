/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.picocontainer.monitors.AbstractComponentMonitor;
import org.picocontainer.monitors.DefaultComponentMonitor;
import org.picocontainer.ComponentMonitor;


/**
 * A {@link org.picocontainer.ComponentMonitor} which writes to a Log4J {@link org.apache.log4j.Logger} instance.
 * The Logger instance can either be injected or, if not set, the {@link LogManager LogManager}
 * will be used to retrieve it at every invocation of the monitor.
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision: $
 */
public class Log4JComponentMonitor extends AbstractComponentMonitor implements Serializable {

    private Logger logger;
    private final ComponentMonitor delegate;

    /**
     * Creates a Log4JComponentMonitor with no Logger instance set.
     * The {@link LogManager LogManager} will be used to retrieve the Logger instance
     * at every invocation of the monitor.
     */
    public Log4JComponentMonitor() {
        delegate = new DefaultComponentMonitor();
    }
    
    /**
     * Creates a Log4JComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     */
    public Log4JComponentMonitor(Class loggerClass) {
        this(loggerClass.getName());
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance name. It uses the
     * {@link org.apache.log4j.LogManager LogManager} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     */
    public Log4JComponentMonitor(String loggerName) {
        this(LogManager.getLogger(loggerName));
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     */
    public Log4JComponentMonitor(Logger logger) {
        this();
        this.logger = logger;
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     */
    public Log4JComponentMonitor(Class loggerClass, ComponentMonitor delegate) {
        this(loggerClass.getName(), delegate);
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance name. It uses the
     * {@link org.apache.log4j.LogManager LogManager} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     */
    public Log4JComponentMonitor(String loggerName, ComponentMonitor delegate) {
        this(LogManager.getLogger(loggerName), delegate);
    }

    /**
     * Creates a Log4JComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     */
    public Log4JComponentMonitor(Logger logger, ComponentMonitor delegate) {
        this(delegate);
        this.logger = logger;
    }

    public Log4JComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    public void instantiating(Constructor constructor) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INSTANTIATING, new Object[]{toString(constructor)}));
        }
        delegate.instantiating(constructor);
    }

    public void instantiated(Constructor constructor, long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INSTANTIATED, new Object[]{toString(constructor), new Long(duration)}));
        }
        delegate.instantiated(constructor, duration);
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] parameters, long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INSTANTIATED2, new Object[]{toString(constructor), new Long(duration), instantiated.getClass().getName(), toString(parameters)}));
        }
        delegate.instantiated(constructor, instantiated, parameters, duration);
    }

    public void instantiationFailed(Constructor constructor, Exception cause) {
        Logger logger = getLogger(constructor);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(INSTANTIATION_FAILED, new Object[]{toString(constructor), cause.getMessage()}), cause);
        }
        delegate.instantiationFailed(constructor, cause);
    }

    public void invoking(Method method, Object instance) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INVOKING, new Object[]{toString(method), instance}));
        }
        delegate.invoking(method, instance);
    }

    public void invoked(Method method, Object instance, long duration) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INVOKED, new Object[]{toString(method), instance, new Long(duration)}));
        }
        delegate.invoked(method, instance, duration);
    }

    public void invocationFailed(Method method, Object instance, Exception cause) {
        Logger logger = getLogger(method);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(INVOCATION_FAILED, new Object[]{toString(method), instance, cause.getMessage()}), cause);
        }
        delegate.invocationFailed(method, instance, cause);
    }

    public void lifecycleInvocationFailed(Method method, Object instance, RuntimeException cause) {
        Logger logger = getLogger(method);
        if (logger.isEnabledFor(Priority.WARN)) {
            logger.warn(format(LIFECYCLE_INVOCATION_FAILED, new Object[]{toString(method), instance, cause.getMessage()}), cause);
        }
        delegate.lifecycleInvocationFailed(method, instance, cause);
    }

    protected Logger getLogger(Member member) {
        if ( logger != null ){
            return logger;
        } 
        return LogManager.getLogger(member.getDeclaringClass());
    }

}
