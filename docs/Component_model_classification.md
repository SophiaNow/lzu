# Classification Framework (Crnkovic, 2011)

## Lifecycle Dimension
- Modelling: does not provide support for any specific modelling language, neither specifies models of components
- Implementation: Uses the Java language and JVM etc. for code production, production of executables..
- Packaging: Supports JAR  packaged components
- Deployment: Components can be deployed at runtime

| Modelling | Implementation | Packaging | Deployment      |
|-----------|----------------|-----------|-----------------|
| No        | Yes            | Yes (JAR) | Yes, at runtime |


## Construction Dimension
### Interface Specification
- the component model does not support interface specification of components 
- Required Interface: Annotation, Provided: start/stop methods, state request
- Interface level: On a syntactic level

| Interface type | Distinction of Provides/Requires | Distinctive Feature | Interface Language | Interface Levels (Syntactic, Semantic, Behaviour) |
|----------------|----------------------------------|---------------------|--------------------|---------------------------------------------------|
| N/A            | Yes                              | No                  | No                 | Syntactic Level                                   |

### Binding and Interactions
- The binding is exogenous as the runtime orchestrates the components
- The binding is vertical as the components satisfies the rules of the component model
- As the components are run on a separate thread and the main thread is independently run the communication type is asynchronous

| Binding   |          | Interactions       |                     |
|-----------|----------|--------------------|---------------------|
| Exogenous | Vertical | Interaction Styles | Communication Types |
| No        | Yes      | N/A                | asynchronous        |