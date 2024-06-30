package com.coding2themax.petstore.data.validation;

import java.util.List;

import org.openapitools.client.model.Pet;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PetStatusConstraintValidator implements ConstraintValidator<PetStatusContraint, List<String>> {

  @Override
  public boolean isValid(List<String> status, ConstraintValidatorContext ctx) {
    // log.info(status.toString());

    if (status == null) {
      return false;
    } else {
      for (String s : status) {
        log.info("Status: {}", s);
        if (!Pet.StatusEnum.AVAILABLE.getValue().equals(s) && !Pet.StatusEnum.PENDING.getValue().equals(s)
            && !Pet.StatusEnum.SOLD.getValue().equals(s)) {
          return false;
        }
      }
      return true;
    }
  }

}
