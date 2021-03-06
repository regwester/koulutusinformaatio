package fi.vm.sade.koulutusinformaatio.domain;

/**
 * @author Hannu Lyytikainen
 */
public class EmphasizedSubject {

    private String value;
    private I18nText subject;

    public EmphasizedSubject() {
    }

    public EmphasizedSubject(I18nText subject, String value) {
        this.setSubject(subject);
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public I18nText getSubject() {
        return subject;
    }

    public void setSubject(I18nText subject) {
        this.subject = subject;
    }
}