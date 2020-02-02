package com.meetsid.userApp.Utils.MRZ.Types;

import com.meetsid.userApp.Utils.MRZ.MrzParseException;
import com.meetsid.userApp.Utils.MRZ.MrzRange;

public enum MrzDocumentCode {
    Passport,
    TypeI,
    TypeA,
    CrewMember,
    TypeC,
    TypeV,
    Migrant;

    public static MrzDocumentCode parse(String mrz) {
        final String code = mrz.substring(0, 2);

        // 2-letter checks
        switch (code) {
            case "IV":
                throw new MrzParseException("IV document code is not allowed", mrz, new MrzRange(0, 2, 0), null); // TODO why?
            case "AC":
                return CrewMember;
            case "ME":
                return Migrant;
            case "TD":
                return Migrant; // travel document
            case "IP":
                return Passport;
        }

        // 1-letter checks
        switch (code.charAt(0)) {
            case 'T':   // usually Travel Document
            case 'P':
                return Passport;
            case 'A':
                return TypeA;
            case 'C':
                return TypeC;
            case 'V':
                return TypeV;
            case 'I':
                return TypeI; // identity card or residence permit
            case 'R':
                return Migrant;  // swedish '51 Convention Travel Document
        }


        throw new MrzParseException("Unsupported document code: " + code, mrz, new MrzRange(0, 2, 0), null);
    }
}
