package me.linx.vchat.utils;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValidationUtils {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> ValidationResult validateEntity(T obj) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        // if( CollectionUtils.isNotEmpty(set) ){
        if (set != null && set.size() != 0) {
            result.setHasErrors(true);
            Map<String, String> errorMsg = new HashMap<String, String>();
            for (ConstraintViolation<T> cv : set) {
                errorMsg.put(cv.getPropertyPath().toString(), cv.getMessage());
            }
            result.setErrorMsg(errorMsg);
        }
        return result;
    }

    public static <T> ValidationResult validateProperty(T obj, String propertyName) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<T>> set = validator.validateProperty(obj, propertyName, Default.class);
        if (set != null && set.size() != 0) {
            result.setHasErrors(true);
            Map<String, String> errorMsg = new HashMap<String, String>();
            for (ConstraintViolation<T> cv : set) {
                errorMsg.put(propertyName, cv.getMessage());
            }
            result.setErrorMsg(errorMsg);
        }
        return result;
    }


    public static class ValidationResult {

        // 校验结果是否有错
        private boolean hasErrors;

        // 校验错误信息
        private Map<String, String> errorMsg;

        private void setHasErrors(boolean hasErrors) {
            this.hasErrors = hasErrors;
        }

        public boolean hasErrors(){
            return hasErrors;
        }

        private void setErrorMsg(Map<String, String> errorMsg) {
            this.errorMsg = errorMsg;
        }

        public Map<String, String> errorMsg() {
            return errorMsg;
        }

        public String errorFormatMsg(){
            if (hasErrors && errorMsg != null && !errorMsg.isEmpty()){
                StringBuilder sb = new StringBuilder();
                for (String value : errorMsg.values()) {
                    sb.append(value).append("; ");
                }
                return sb.toString();
            }
            return null;
        }
    }

}
