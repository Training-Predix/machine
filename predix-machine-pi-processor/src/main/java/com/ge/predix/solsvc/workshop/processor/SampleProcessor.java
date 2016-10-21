/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.workshop.processor;

import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

import com.ge.dspmicro.hoover.api.processor.IProcessor;
import com.ge.dspmicro.hoover.api.processor.ProcessorException;
import com.ge.dspmicro.hoover.api.spillway.ITransferData;
import com.ge.dspmicro.machinegateway.types.ITransferable;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PQuality;

/**
 * This class provides a Processor implementation which will process the data as per configuration on the spillway.
 */
@Component(name = SampleProcessor.SERVICE_PID, provide =
{
    IProcessor.class
})
public class SampleProcessor
        implements IProcessor
{
    /**
     * Service PID
     */
    public static final String SERVICE_PID = "com.ge.dspmicro.sample.hoover.processor";                  //$NON-NLS-1$

    /** Create logger to report errors, warning massages, and info messages (runtime Statistics) */
    protected static Logger   _logger = LoggerFactory.getLogger(SampleProcessor.class);
    
    public static final String printData = "printData";
    
    PQuality quality;
    
    /**
     * @param ctx context of the bundle.
     */
    @Activate
    public void activate(ComponentContext ctx)
    {
    	_logger.info("Spillway service activated."); //$NON-NLS-1$
		quality = new PQuality(PQuality.QualityEnum.BAD);

    }
    

    /**
     * @param ctx context of the bundle.
     */
    @Deactivate
    public void deactivate(ComponentContext ctx)
    {
        
        if ( _logger.isDebugEnabled() )
        {
            _logger.debug("Spillway service deactivated."); //$NON-NLS-1$
        }
    }
 
    @Override
    public void processValues(String processType, List<ITransferable> values, ITransferData transferData)
            throws ProcessorException
    {   	
    	for (ITransferable value : values)
		{
			if (value instanceof PDataValue)
			{
				_logger.info("IN Value:" + value.toString() );
				
				if (((PDataValue) value).getNodeName().contains("Light"))
				{
					Double x = (Double) ((PDataValue) value).getEnvelope().getValue();
					if (x <= 50){	
						((PDataValue) value).setQuality(quality);
					}
						
				} else if (((PDataValue) value).getNodeName().contains("TempAndHumidity")) 
					{
						// convert from Celsius to Fahrenheit
						double temp = (Double) ((PDataValue) value).getEnvelope().getValue();
						_logger.info("Temp coming in: " + temp);
						temp = Math.round(temp) * 1.8 + 32;
						_logger.info("Temp converted to: " + temp);
						((PDataValue) value).getEnvelope().setValue(temp);
					}

			} else {
		    	_logger.info("ERROR in PDataValue" +values.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			}
				
		}
    	_logger.info("TRANSFER values :" +values.toString()); //$NON-NLS-1$ //$NON-NLS-2$
    	transferData.transferData(values);
    	
    }
   
}
