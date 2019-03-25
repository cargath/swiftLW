package de.cargath.codegen;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import io.swagger.codegen.*;
import io.swagger.codegen.CodegenModel;

import io.swagger.models.ModelImpl;
import io.swagger.models.properties.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Converts text in a fragment to title case.
 *
 * Register:
 * <pre>
 * additionalProperties.put("titlecase", new TitlecaseLambda());
 * </pre>
 *
 * Use:
 * <pre>
 * {{#titlecase}}{{classname}}{{/titlecase}}
 * </pre>
 */
class TitlecaseLambda implements Mustache.Lambda  {
    private String delimiter;

    /**
     * Constructs a new instance of {@link TitlecaseLambda}, which will convert all text
     * in a space delimited string to title-case.
     */
    public TitlecaseLambda() {
        this(" ");
    }

    /**
     * Constructs a new instance of {@link TitlecaseLambda}, splitting on the specified
     * delimiter and converting each word to title-case.
     *
     * NOTE: passing {@code null} results in a title-casing the first word only.
     *
     * @param delimiter Provided to allow an override for the default space delimiter.
     */
    public TitlecaseLambda(String delimiter) {
        this.delimiter = delimiter;
    }

    private String titleCase(final String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public void execute(Template.Fragment fragment, Writer writer) throws IOException {
        String text = fragment.execute();
        if (delimiter == null) {
            writer.write(titleCase(text));
            return;
        }

        // Split accepts regex. \Q and \E wrap the delimiter to create a literal regex,
        // so things like "." and "|" aren't treated as their regex equivalents.
        String[] parts = text.split("\\Q" + delimiter + "\\E");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            writer.write(titleCase(part));
            if (i != parts.length - 1) {
                writer.write(delimiter);
            }
        }
    }
}

public class SwiftLWGenerator extends DefaultCodegen implements CodegenConfig {

    //
    public static final String OBJC_COMPATIBLE = "objcCompatible";
    public static final String PROJECT_NAME = "projectName";

    protected boolean objcCompatible = false;
    protected String projectName = "Network";

    // source folder where to write the files
    protected String sourceFolder = "Source";
    protected String apiVersion = "1.0.0";

    /**
     * Configures the type of generator.
     *
     * @return The CodegenType for this generator.
     * @see    io.swagger.codegen.CodegenType
     */
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    /**
     * Configures a friendly name for the generator.
     * This will be used by the generator to select the library with the -l flag.
     *
     * @return The friendly name for the generator.
     */
    public String getName() {
        return "swiftLW";
    }

    /**
     * Returns human-friendly help for the generator.
     * Provide the consumer with help tips, parameters here.
     *
     * @return A string value for the help message
     */
    public String getHelp() {
        return "Generates a Swift client library without third-party dependencies.";
    }

    public SwiftLWGenerator() {
        super();

        // set the output folder here
        outputFolder = "generated-code" + File.separatorChar + "swiftLW";

        /**
         * Models.
         * You can write model files using the modelTemplateFiles map.
         * If you want to create one template for file, you can do so here.
         * For multiple files for model, just put another entry in the `modelTemplateFiles` with a different extension.
         */
        // template to use, extension for each file to write
        modelTemplateFiles.put("model.mustache", ".swift");

        /**
         * API classes.
         * You can write classes for each Api file with the apiTemplateFiles map.
         * As with models, add multiple entries with different extensions for multiple files per class.
         */
        // template to use, extension for each file to write
        apiTemplateFiles.put("operation.mustache", ".swift");
        apiTemplateFiles.put("queryItem.mustache", "QueryItem.swift");

        /**
         * Template Location.
         * This is the location which templates will be read from.
         * The generator will use the resource stream to attempt to read the templates.
         */
        templateDir = "swiftLW";

        /**
         * API Package.
         * Optional, if needed, this can be used in templates.
         */
        apiPackage = "Operations"; // "io.swagger.client.api";

        /**
         * Model Package.
         * Optional, if needed, this can be used in templates.
         */
        modelPackage = "Models"; // "io.swagger.client.model";

        /**
         * Reserved words.
         * Override this with reserved words specific to your language.
         */
        reservedWords = new HashSet<String> (
            // replace with static values
            Arrays.asList("sample1", "sample2")
        );

        /**
         * Additional Properties.
         * These values can be passed to the templates and are available in models, apis, and supporting files.
         */
        additionalProperties.put("apiVersion", apiVersion);

        /**
         * Language Specific Primitives.
         * These types will not trigger imports by the client generator.
         */
        languageSpecificPrimitives = new HashSet<String>(
            // replace these with your types
            Arrays.asList("Type1", "Type2")
        );

        // Swift type names
        typeMapping = new HashMap<>();
        typeMapping.put("array", "Array");
        typeMapping.put("List", "Array");
        typeMapping.put("map", "Dictionary");
        typeMapping.put("date", "Date");
        typeMapping.put("Date", "Date");
        typeMapping.put("DateTime", "Date");
        typeMapping.put("boolean", "Bool");
        typeMapping.put("string", "String");
        typeMapping.put("char", "Character");
        typeMapping.put("short", "Int");
        typeMapping.put("int", "Int");
        typeMapping.put("long", "Int64");
        typeMapping.put("integer", "Int");
        typeMapping.put("Integer", "Int");
        typeMapping.put("float", "Float");
        typeMapping.put("number", "Double");
        typeMapping.put("double", "Double");
        typeMapping.put("object", "Any");
        typeMapping.put("file", "URL");
        typeMapping.put("binary", "Data");
        typeMapping.put("ByteArray", "Data");
        typeMapping.put("UUID", "UUID");

        // additionalProperties.put("camelcase", new CamelCaseLambda());
        // additionalProperties.put("indent2", new IndentedLambda(2));
        // additionalProperties.put("indent4", new IndentedLambda(4));
        // additionalProperties.put("indent8", new IndentedLambda(8));
        // additionalProperties.put("lowercase", new LowercaseLambda());
        additionalProperties.put("titlecase", new TitlecaseLambda());
        // additionalProperties.put("uppercase", new UppercaseLambda());

        cliOptions.add(new CliOption(PROJECT_NAME, "Project name in Xcode"));
    }

    @Override
    protected void addAdditionPropertiesToCodeGenModel(CodegenModel codegenModel, ModelImpl swaggerModel) {
        final Property additionalProperties = swaggerModel.getAdditionalProperties();
        if (additionalProperties != null) {
            codegenModel.additionalPropertiesType = getSwaggerType(additionalProperties);
        }
    }

    @Override
    public void processOpts() {
        super.processOpts();

        // Setup project name.
        if (additionalProperties.containsKey(PROJECT_NAME)) {
            setProjectName((String) additionalProperties.get(PROJECT_NAME));
        } else {
            additionalProperties.put(PROJECT_NAME, projectName);
        }

        // Nest source folder under project name.
        sourceFolder = projectName + File.separator + sourceFolder;

        // Setup objcCompatible option,
        // which adds additional properties and methods for Objective-C compatibility.
        if (additionalProperties.containsKey(OBJC_COMPATIBLE)) {
            setObjcCompatible(convertPropertyToBooleanAndWriteBack(OBJC_COMPATIBLE));
        }
        additionalProperties.put(OBJC_COMPATIBLE, objcCompatible);

        /**
         * Supporting Files.
         * You can write single files for the generator with the entire object tree available.
         * If the input file has a suffix of `.mustache it will be processed by the template engine.
         * Otherwise, it will be copied.
         */
        supportingFiles.add(new SupportingFile("Client.mustache", sourceFolder, projectName + "Client.swift"));
        supportingFiles.add(new SupportingFile("Error.mustache", sourceFolder, projectName + "Error.swift"));
        supportingFiles.add(new SupportingFile("String.mustache", sourceFolder, "Extensions" + File.separatorChar + "String+" + projectName + ".swift"));
        supportingFiles.add(new SupportingFile("URL.mustache", sourceFolder, "Extensions" + File.separatorChar + "URL+" + projectName + ".swift"));
        supportingFiles.add(new SupportingFile("URLComponents.mustache", sourceFolder, "Extensions" + File.separatorChar + "URLComponents+" + projectName + ".swift"));
        supportingFiles.add(new SupportingFile("URLRequest.mustache", sourceFolder, "Extensions" + File.separatorChar + "URLRequest+" + projectName + ".swift"));
        supportingFiles.add(new SupportingFile("URLSession.mustache", sourceFolder, "Extensions" + File.separatorChar + "URLSession+" + projectName + ".swift"));
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setObjcCompatible(boolean objcCompatible) {
        this.objcCompatible = objcCompatible;
    }

    /**
     * Escapes a reserved word as defined in the `reservedWords` array.
     * Handle escaping those terms here.
     * This logic is only called if a variable matches the reserved words.
     *
     * @return the escaped term
     */
    @Override
    public String escapeReservedWord(String name) {
        // add an underscore to the name
        return "_" + name;
    }

    @Override
    public String escapeQuotationMark(String input) {
        // remove " to avoid code injection
        return input.replace("\"", "");
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input.replace("*/", "*_/").replace("/*", "/_*");
    }

    /**
     * Location to write model files.
     * You can use the modelPackage() as defined when the class is instantiated.
     */
    public String modelFileFolder() {
        return outputFolder + File.separatorChar + sourceFolder + File.separatorChar + modelPackage().replace('.', File.separatorChar);
    }

    /**
     * Location to write API files.
     * You can use the apiPackage() as defined when the class is instantiated.
     */
    @Override
    public String apiFileFolder() {
        return outputFolder + File.separatorChar + sourceFolder + File.separatorChar + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String toApiName(String name) {
        if (name.length() == 0) {
            return "DefaultAPI";
        }
        return initialCaps(name); // + "API";
    }

    @Override
    public String toApiFilename(String name) {
        if (name.length() == 0) {
            return "DefaultAPI";
        } /* else if (name.contains("Member")) {
            return initialCaps(name); // + "API";
        } */ else {
            return projectName + "Client+" + initialCaps(name); // + "API";
        }
    }

    @Override
    public String toDefaultValue(Property prop) {
        // nil
        return null;
    }

    @Override
    public String toEnumValue(String value, String datatype) {
        return String.valueOf(value);
    }

    @Override
    public String toEnumDefaultValue(String value, String datatype) {
        return datatype + "_" + value;
    }

    @Override
    public String toEnumVarName(String name, String datatype) {
        if (name.length() == 0) {
            return "empty";
        }

        Pattern startWithNumberPattern = Pattern.compile("^\\d+");
        Matcher startWithNumberMatcher = startWithNumberPattern.matcher(name);
        if (startWithNumberMatcher.find()) {
            String startingNumbers = startWithNumberMatcher.group(0);
            String nameWithoutStartingNumbers = name.substring(startingNumbers.length());
            return "_" + startingNumbers + camelize(nameWithoutStartingNumbers, true);
        }

        // for symbol, e.g. $, #
        if (getSymbolName(name) != null) {
            return camelize(WordUtils.capitalizeFully(getSymbolName(name).toUpperCase()), true);
        }

        // Camelize only when we have a structure defined below
        Boolean camelized = false;
        if (name.matches("[A-Z][a-z0-9]+[a-zA-Z0-9]*")) {
            name = camelize(name, true);
            camelized = true;
        }

        // Reserved Name
        String nameLowercase = StringUtils.lowerCase(name);
        if (isReservedWord(nameLowercase)) {
            return escapeReservedWord(nameLowercase);
        }

        // Check for numerical conversions
        if ("Int".equals(datatype) || "Int32".equals(datatype) || "Int64".equals(datatype) || "Float".equals(datatype) || "Double".equals(datatype)) {
            String varName = "number" + camelize(name);
            varName = varName.replaceAll("-", "minus");
            varName = varName.replaceAll("\\+", "plus");
            varName = varName.replaceAll("\\.", "dot");
            return varName;
        }

        // If we have already camelized the word, don't progress
        // any further
        if (camelized) {
            return name;
        }

        char[] separators = {'-', '_', ' ', ':', '(', ')'};
        return camelize(WordUtils.capitalizeFully(StringUtils.lowerCase(name), separators).replaceAll("[-_ :\\(\\)]", ""), true);
    }

    @Override
    public String toEnumName(CodegenProperty property) {
        String enumName = toModelName(property.name);

        // Ensure that the enum type doesn't match a reserved word or
        // the variable name doesn't match the generated enum type or the
        // Swift compiler will generate an error
        if (isReservedWord(property.datatypeWithEnum) || toVarName(property.name).equals(property.datatypeWithEnum)) {
            enumName = property.datatypeWithEnum + "Enum";
        }

        // TODO: toModelName already does something for names starting with number,
        // so this code is probably never called
        if (enumName.matches("\\d.*")) { // starts with number
            return "_" + enumName;
        } else {
            return enumName;
        }
    }

    @Override
    public String toInstantiationType(Property prop) {
        if (prop instanceof MapProperty) {
            MapProperty ap = (MapProperty) prop;
            String inner = getSwaggerType(ap.getAdditionalProperties());
            return inner;
        } else if (prop instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) prop;
            String inner = getSwaggerType(ap.getItems());
            return "[" + inner + "]";
        }
        return null;
    }

    @Override
    public String toOperationId(String operationId) {
        operationId = camelize(sanitizeName(operationId), true);

        // Throw exception if method name is empty.
        // This should not happen but keep the check just in case
        if (StringUtils.isEmpty(operationId)) {
            throw new RuntimeException("Empty method name (operationId) not allowed");
        }

        // method name cannot use reserved keyword, e.g. return
        if (isReservedWord(operationId)) {
            String newOperationId = camelize(("call_" + operationId), true);
            LOGGER.warn(operationId + " (reserved word) cannot be used as method name." + " Renamed to " + newOperationId);
            return newOperationId;
        }

        return operationId;
    }

    /**
     * Optional - type declaration.
     * This is a String which is used by the templates to instantiate your types.
     * There is typically special handling for different property types.
     *
     * @return A string value used as the `dataType` field for model templates, `returnType` for api templates.
     */
    @Override
    public String getTypeDeclaration(Property prop) {
        if (prop instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) prop;
            Property inner = ap.getItems();
            return "[" + getTypeDeclaration(inner) + "]";
        } else if (prop instanceof MapProperty) {
            MapProperty mp = (MapProperty) prop;
            Property inner = mp.getAdditionalProperties();
            return "[String: " + getTypeDeclaration(inner) + "]";
        }
        return super.getTypeDeclaration(prop);
    }

    /**
     * Optional - swagger type conversion.
     * This is used to map swagger types in a `Property` into either language specific types via `typeMapping` or into complex models if there is not a mapping.
     *
     * @return A string value of the type or complex model for this property.
     * @see io.swagger.models.properties.Property
     */
    @Override
    public String getSwaggerType(Property prop) {
        String swaggerType = super.getSwaggerType(prop);
        String type;
        if (typeMapping.containsKey(swaggerType)) {
            type = typeMapping.get(swaggerType);
            if (languageSpecificPrimitives.contains(type) || defaultIncludes.contains(type)) {
                return type;
            }
        } else {
            type = swaggerType;
        }
        return toModelName(type);
    }

    /**
     * We iterate through the list of models, and also iterate through each of the properties for each model.
     *
     * For each property, if:
     * CodegenProperty.name != CodegenProperty.baseName
     *
     * then we set:
     * CodegenProperty.vendorExtensions["x-codegen-escaped-property-name"] = true
     *
     * Also, if any property in the model has:
     * x-codegen-escaped-property-name = true
     *
     * then we mark:
     * CodegenModel.vendorExtensions["x-codegen-has-escaped-property-names"] = true
     */
    @Override
    public Map<String, Object> postProcessModels(Map<String, Object> objs) {
        Map<String, Object> postProcessedModelsEnum = postProcessModelsEnum(objs);
        List<Object> models = (List<Object>) postProcessedModelsEnum.get("models");
        for (Object _mo : models) {
            Map<String, Object> mo = (Map<String, Object>) _mo;
            CodegenModel cm = (CodegenModel) mo.get("model");
            boolean modelHasPropertyWithEscapedName = false;
            for (CodegenProperty prop : cm.allVars) {
                if (!prop.name.equals(prop.baseName)) {
                    prop.vendorExtensions.put("x-codegen-escaped-property-name", true);
                    modelHasPropertyWithEscapedName = true;
                }
            }
            if (modelHasPropertyWithEscapedName) {
                cm.vendorExtensions.put("x-codegen-has-escaped-property-names", true);
            }
        }
        return postProcessedModelsEnum;
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);

        // The default template code has the following logic for
        // assigning a type as Swift Optional:
        //
        // {{^unwrapRequired}}?{{/unwrapRequired}}
        // {{#unwrapRequired}}{{^required}}?{{/required}}{{/unwrapRequired}}
        //
        // which means:
        //
        // boolean isSwiftOptional = !unwrapRequired || (unwrapRequired && !property.required);
        //
        // We can drop the check for unwrapRequired in (unwrapRequired && !property.required)
        // due to short-circuit evaluation of the || operator.
        boolean isSwiftOptional = !property.required; // !unwrapRequired || !property.required;
        boolean isSwiftScalarType = property.isInteger || property.isLong || property.isFloat || property.isDouble || property.isBoolean;
        if (isSwiftOptional && isSwiftScalarType) {
            // Optional scalar types like Int?, Int64?, Float?, Double?, and Bool?
            // do not translate to Objective-C. So we want to flag those
            // properties in case we want to put special code in the templates
            // which provide Objective-C compatibility.
            property.vendorExtensions.put("x-swift-optional-scalar", true);
        }
    }

}
