package com.easyvisa

import com.easyvisa.utils.StringUtils

trait EasyVisaIdGeneratable {

    Profile profile

    abstract String getEasyVisaIdPrefix()

    abstract String getSequenceName()

    abstract SqlService getSqlService()

    String getEasyVisaId()  {
        "${getEasyVisaIdPrefix()}${StringUtils.padEasyVisaId(getSqlService().getNextSequenceId(getSequenceName()))}"
    }

}
