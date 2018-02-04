package uk.gov.digital.ho.egar.submission.utils.impl;

import com.fincore.cbp.stt.xml.T_Address;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.submission.utils.AddressBuilderUtils;

@Component
public class AddressBuilderUtilsImpl implements AddressBuilderUtils {

    public String buildAddressAsString(T_Address fullAddress) {
        StringBuilder addBuilder = new StringBuilder();
        if (fullAddress.getAddressField()!=null) {
            for (String addFld : fullAddress.getAddressField()) {
                addBuilder.append(addFld);
                addBuilder.append(System.lineSeparator());
            }
        }
        if (fullAddress.getAddressPostCode()!=null) {
            addBuilder.append(fullAddress.getAddressPostCode());
            addBuilder.append(System.lineSeparator());
        }
        if (fullAddress.getAddressCountry()!=null) {
            addBuilder.append(fullAddress.getAddressCountry());
        }
        return addBuilder.toString();
    }
}
