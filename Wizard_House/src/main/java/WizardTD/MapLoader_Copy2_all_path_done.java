package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// this class is for the basic map loaded for the application
// which starts with loading the grass as the default
// then load anything else should be on the map
// all the map elements have be saved under the resources,
//the same path as movable objects, such as enemy
// during the Board, floor need to be loaded first
// then add elements onto the map: "X" is the path, "S" is the shrub, "W" is the House


public class MapLoader_Copy2_all_path_done {
    int[][] grid;
    PApplet pApplet;
    String map_resources_path;
    PImage floor; // the default background for the map, which should be "Grass"
    PImage path0, path1, path2, path3; // path0 is the default setting for the path
    // in this project, path0 is the one-fork path, path1 is the two-ways path, and so on
    // thus, the path need linked with the other path, or the house
    // also, if the path close to the boarder, there should be a way to the boarder as well
    PImage shrub; // cells cannot replace by any object
    PImage house; // the end point of the map
    App App;

    public MapLoader_Copy2_all_path_done(PApplet p, String map_resources_path) {
        this.pApplet = p;
        this.map_resources_path = map_resources_path;
        App = new App();

        // Converting BufferedImage to PImage
        floor = pApplet.loadImage("src/main/resources/WizardTD/grass.png");

        path0 = pApplet.loadImage("src/main/resources/WizardTD/path0.png");
        path1 = pApplet.loadImage("src/main/resources/WizardTD/path1.png");
        path2 = pApplet.loadImage("src/main/resources/WizardTD/path2.png");
        path3 = pApplet.loadImage("src/main/resources/WizardTD/path3.png");

        shrub = pApplet.loadImage("src/main/resources/WizardTD/shrub.png");
        house = pApplet.loadImage("src/main/resources/WizardTD/wizard_house.png");

        loadMap();
    }

//    private void loadMap() {
//
//    }

    private void loadMap() {
        try {
            Scanner scanner = new Scanner(new File(map_resources_path));
            List<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            int rows = lines.size();
            int cols = 0;

            // Find the maximum column count
            for(String line : lines) {
                cols = Math.max(cols, line.length());
            }

            // Initialize the grid with proper dimensions
            grid = new int[rows][cols];

            for (int r = 0; r < rows; r++) {
                char[] chars = lines.get(r).toCharArray();
                for (int c = 0; c < cols; c++) {
                    if (c < chars.length) {
                        switch (chars[c]) {
                            case 'X':
                                grid[r][c] = 1; // Path
                                break;
                            case 'S':
                                grid[r][c] = 2; // Shrub
                                break;
                            case 'W':
                                grid[r][c] = 3; // House
                                break;
                            default:
                                grid[r][c] = 0; // Default to Grass
                                break;
                        }
                    } else {
                        grid[r][c] = 0; // Grass
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle error - perhaps display a message to the user or use a default map
        }
    }

    public void draw() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Always draw grass first as the default background
                pApplet.image(floor, j * App.CELLSIZE, i * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);

                // Then draw the element specified in the grid on top of the grass
                switch (grid[i][j]) {
                    case 1: // Path
                        PImage pathImage = determinePathImage(i, j);
                        pApplet.image(pathImage, j * App.CELLSIZE, i * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                        break;
                    case 2: // Shrub
                        pApplet.image(shrub, j * App.CELLSIZE, i * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                        break;
                    case 3: // House
                        pApplet.image(house, j * App.CELLSIZE, i * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                        break;
                }
            }
        }
    }

    private PImage determinePathImage(int i, int j) {
        boolean pathAbove = i > 0 && grid[i-1][j] == 1;
        boolean pathBelow = i < grid.length - 1 && grid[i+1][j] == 1;
        boolean pathLeft = j > 0 && grid[i][j-1] == 1;
        boolean pathRight = j < grid[i].length - 1 && grid[i][j+1] == 1;

        boolean shrubBelow = i < grid.length - 1 && grid[i+1][j] == 2;
        boolean shrubAbove = i > 0 && grid[i-1][j] == 2;
        boolean shrubLeft = j > 0 && grid[i][j-1] == 2;
        boolean shrubRight = j < grid[i].length - 1 && grid[i][j+1] == 2;
        boolean houseAbove = i > 0 && grid[i-1][j] == 3;
        boolean houseBelow = i < grid.length - 1 && grid[i+1][j] == 3;
        boolean houseLeft = j > 0 && grid[i][j-1] == 3;
        boolean houseRight = j < grid[i].length - 1 && grid[i][j+1] == 3;

        boolean atTopBorder = i == 0;
        boolean atBottomBorder = i == grid.length - 1;
        boolean atLeftBorder = j == 0;
        boolean atRightBorder = j == grid[i].length - 1;

        PImage pathImage = path0; // Default path image
        float rotationAngle = 0; // Default rotation

        // Conditions for paths:
        if (pathAbove && pathBelow && pathLeft && pathRight) {
            pathImage = path3;
        } else if (pathAbove && pathBelow && pathLeft) {
            pathImage = path2;
            rotationAngle = 90;
        } else if (pathAbove && pathLeft && pathRight) {
            pathImage = path2;
            rotationAngle = 180;
        } else if (pathBelow && pathLeft && pathRight) {
            pathImage = path2;
        } else if (pathAbove && pathBelow && pathRight) {
            pathImage = path2;
            rotationAngle = 270;
        } else if (pathAbove && pathBelow) {
            pathImage = path0;
            rotationAngle = 90;
        } else if (pathLeft && pathRight) {
            pathImage = path0;
        } else if (pathBelow && pathLeft) {
            pathImage = path1;
        } else if (pathAbove && pathLeft) {
            pathImage = path1;
            rotationAngle = 90;
        } else if (pathBelow && pathRight) {
            pathImage = path1;
            rotationAngle = 270;
        } else if (pathAbove && pathRight) {
            pathImage = path1;
            rotationAngle = 180;
        }

        // Conditions for shrubs and houses:
        else if (pathAbove && !pathBelow && !pathLeft && !pathRight && shrubBelow) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (!pathAbove && pathBelow && !pathLeft && !pathRight && shrubAbove) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (!pathAbove && !pathBelow && pathLeft && !pathRight && shrubRight) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (!pathAbove && !pathBelow && !pathLeft && pathRight && shrubLeft) {
            pathImage = path0;
            rotationAngle = 0;
        }
        else if (!pathAbove && !pathBelow && !pathLeft && pathRight && houseLeft) {
            pathImage = path0;
            rotationAngle = 0;
        }
        else if (pathAbove && !pathBelow && !pathLeft && !pathRight && houseLeft) {
            pathImage = path1;
            rotationAngle = 90;
        }
        else if (!pathAbove && pathBelow && !pathLeft && !pathRight && houseLeft) {
            pathImage = path1;
            rotationAngle = 270;
        }
        // Conditions for house below:
        else if (!pathAbove && !pathBelow && pathLeft && !pathRight && houseBelow) {
            pathImage = path0;
            rotationAngle = 0;
        }
        else if (!pathAbove && !pathBelow && !pathLeft && pathRight && houseBelow) {
            pathImage = path0;
            rotationAngle = 180;
        }
        else if (pathAbove && !pathBelow && !pathLeft && !pathRight && houseBelow) {
            pathImage = path0;
            rotationAngle = 90;  // Correcting rotation to 180 degrees
        }



        // Conditions for borders:
        else if (atTopBorder && !pathAbove && pathBelow) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (atBottomBorder && pathAbove && !pathBelow) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (atLeftBorder && !pathLeft && pathRight) {
            pathImage = path0;
        }
        else if (atRightBorder && pathLeft && !pathRight) {
            pathImage = path0;
        }

        return App.rotateImageByDegrees(pathImage, rotationAngle);
    }






}
