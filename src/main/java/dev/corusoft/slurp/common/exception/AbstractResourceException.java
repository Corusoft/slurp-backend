package dev.corusoft.slurp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class AbstractResourceException extends Exception implements Serializable {
}
