package com.zbro.messydoc.testmain;

public enum FileType {


    //d0cf11e0a1b11ae1000000000000000000000000 doc
    //d0cf11e0a1b11ae1000000000000000000000000 ppt
    //d0cf11e0a1b11ae1000000000000000000000000 xls
    //312c4a616d65732c31352c313031310d0a322c52 csv

    UNKNOWN("UNKNOWN"),
    /**
     * JEPG.
     */
    JPEG("FFD8FF"),
    /**
     * CSV.
     */
    CSV("312"),

    /**
     * PNG.
     */
    PNG("89504E47"),

    /**
     * GIF.
     */
    GIF("47494638"),

    /**
     * TIFF.
     */
    TIFF("49492A00"),

    //3135353031313538353732000000000000000000
    TXT("3135"),

    /**
     * Windows Bitmap.
     */
    BMP("424D"),

    /**
     * CAD.
     */
    DWG("41433130"),

    /**
     * Adobe Photoshop.
     */
    PSD("38425053"),

    /**
     * Rich Text Format.
     */
    RTF("7B5C727466"),

    /**
     * XML.
     */
    XML("3C3F786D6C"),

    /**
     * HTML.
     */
    HTML("68746D6C3E"),

    /**
     * Email [thorough only].
     */
    EML("44656C69766572792D646174653A"),

    /**
     * Outlook Express.
     */
    DBX("CFAD12FEC5FD746F"),

    /**
     * Outlook (pst).
     */
    PST("2142444E"),

    /**
     * MS Word/Excel.
     */
    XLS_DOC("D0CF11E0"),

    /**
     * MS Access.
     */
    MDB("5374616E64617264204A"),

    /**
     * WordPerfect.
     */
    WPD("FF575043"),

    /**
     * Postscript.
     */
    EPS("252150532D41646F6265"),

    /**
     * Adobe Acrobat.
     */
    PDF("255044462D312E"),

    /**
     * Quicken.
     */
    QDF("AC9EBD8F"),

    /**
     * Windows Password.
     */
    PWL("E3828596"),

    /**
     * ZIP Archive.
     */
    //504b030414000000080090508155c87b52964404 zip
    //504b0304140000000800664d81555b6dc7fb9733 zip
    ZIP("504B"),

//    /**
//     * WORD PPT EXCEL Archive.
//     */
//    //504b030414000600080000002100e5d9204bb501 xlsx
//    //504b030414000600080000002100a70ceb796801 xlsx
//    //504b0304140006000800000021007d2f880c0b04 pptx
//    //504b030414000600080000002100096a270d7a01 docx
//    OFFICE("504B0304140006"),
//
//    //504B03040A0000000000874EE240000000000000
//    OFFICE2("504B03040A"),

    //0000001866747970617663310000000061766331
    MP4("0000001"),

    //49443303000000000048545045310000000b0000 MP3
    MP3("49443303"),

    //0000002066747970717420202005030071742020
    MOV("0000002"),

    /**
     * RAR Archive.
     */
    RAR("52617221"),

    /**
     * Wave.
     */
    WAV("57415645"),

    /**
     * AVI.
     */
    AVI("41564920"),

    /**
     * Real Audio.
     */
    RAM("2E7261FD"),

    /**
     * Real Media.
     */
    RM("2E524D46"),

    /**
     * MPEG (mpg).
     */
    MPG("000001BA"),

    /**
     * Windows Media.
     */
    ASF("3026B2758E66CF11"),

    GZ("1F8B08"),
    /**
     * MIDI.
     */
    MID("4D546864");

    private String value = "";


    /**
     * Constructor.
     * @param value
     */
    private FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}