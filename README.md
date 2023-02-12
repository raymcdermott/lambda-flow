# Lambda Flow

Make an event flow so that work can be distributed across N Lambdas
where N is the number of work items to be performed.

The event flow will have some simple parameters:
- from: the date from which the works will be collected (inclusive)
- N days: the  number of days after `from` over which the works will be collected (inclusive)

The flow will be in three parts
- initiator: 
  - Triggered manually with the above parameters
  - 1x
  - Lambda which will define the work. It will produce
    - the work items that will be processed by the workers
    - an object that decribes the coordinates of each work item to be processed

- worker:
  - Triggered when the work item object is created by an s3 event
  - Nx - where N depends on how many items are produced
  - Lambda that will do the work. It will produce
    - a result in an object in the location that matches the input (eg X.in will be matched by X.out)

- summarizer:
  - Triggered when the work item result object is created by an s3 event.
  - It will only run when the number of output items match the inputs
  - 1x
  - Lambda that will collate the results. It will produce
    - a report showing the outputs of each worker

