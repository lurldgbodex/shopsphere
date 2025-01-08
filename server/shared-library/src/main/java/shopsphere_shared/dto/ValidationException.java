package shopsphere_shared.dto;

import java.util.Map;

public record ValidationException(Map<String, String> errors) {}
